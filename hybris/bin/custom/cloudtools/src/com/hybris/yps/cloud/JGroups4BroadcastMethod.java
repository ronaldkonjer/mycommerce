package com.hybris.yps.cloud;

import de.hybris.platform.cluster.AbstractBroadcastMethod;
import de.hybris.platform.cluster.BroadcastService;
import de.hybris.platform.cluster.DefaultBroadcastService;
import de.hybris.platform.cluster.RawMessage;
import de.hybris.platform.cluster.jgroups.JGroupsBroadcastMethod;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.config.ConfigIntf;
import org.apache.commons.lang.StringUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Map;

public class JGroups4BroadcastMethod  extends AbstractBroadcastMethod {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsBroadcastMethod.class);
    private static final String BASE_CONFIG_DIR = "jgroups";
    private static final String JGROUPS_UDP_CONFIG_FILE = "jgroups-udp.xml";
    private static final String JGROUPS_TCP_JDBCPING_CONFIG_FILE = "jgroups-tcp.xml";
    private static final String JGROUPS_TCP_MPING_CONFIG_FILE = "jgroups-tcp.xml";
    private static final String DEFAULT_CONFIGURATION = "jgroups-udp.xml";
    private static final String DEFAULT_CHANNEL_NAME = "hybris-broadcast";
    private static final String DEFAULT_TCP_IP = "127.0.0.1";
    private static final String DEFAULT_TCP_PORT = "7800";
    private static final String DEFAULT_MULTICAST_ADDR = "224.0.0.1";
    private static final String DEFAULT_MULTICAST_PORT = "45588";
    private static final String JDBC_PING_CLEAR_TABLE_ON_VIEW_CHANGE = "false";
    private BroadcastService service;
    private volatile JChannel channel;

    public JGroups4BroadcastMethod() {
    }

    public void send(RawMessage message) {
        if (this.channel != null) {
            try {
                this.channel.send((Address)null, message.toRawByteArray());
            } catch (Exception var3) {
                LOG.error("Error during jgroups initialization: ", var3);
            }
        }

    }

    public void shutdown() {
        super.shutdown();
        if (this.channel != null) {
            try {
                this.channel.close();
            } finally {
                this.channel = null;
            }
        }

    }

    public void init(BroadcastService service) {
        super.init(service);
        this.service = service;
        this.channel = this.startChannel();
    }

    private JChannel startChannel() {
        String configuration = this.getJgroupsConfiguration();
        this.setSystemProperties(configuration);
        return this.openChannel(configuration);
    }

    private void setSystemProperties(String configuration) {
        String bindAddr = this.getConfigValue("cluster.broadcast.method.jgroups.tcp.bind_addr", "127.0.0.1");
        String bindPort = this.getConfigValue("cluster.broadcast.method.jgroups.tcp.bind_port", "7800");
        String multicastPort = this.getConfigValue("cluster.broadcast.method.jgroups.udp.mcast_port", "45588");
        String multicastAddr = this.getConfigValue("cluster.broadcast.method.jgroups.udp.mcast_address", "224.0.0.1");
        System.setProperty("hybris.jgroups.bind_addr", bindAddr);
        System.setProperty("hybris.jgroups.bind_port", bindPort);
        System.setProperty("hybris.jgroups.mcast_address", multicastAddr);
        System.setProperty("hybris.jgroups.mcast_port", multicastPort);
        LOG.debug("--- setting up hybris.jgroups.bind_addr to: {}", bindAddr);
        LOG.debug("--- setting up hybris.jgroups.bind_port to: {}", bindPort);
        LOG.debug("--- setting up hybris.jgroups.mcast_addr to: {}", multicastAddr);
        LOG.debug("--- setting up hybris.jgroups.mcast_port to: {}", multicastPort);
        if (this.isTcpConfigurationUsed(configuration)) {
            this.validateTcpConnection(bindAddr, bindPort);
        }

        if (this.isJdbcPingUsed(configuration)) {
            this.setDatabaseSystemProperties();
        }

    }

    private void validateTcpConnection(String ip, String port) {
        try {
            new IpAddress(ip, Integer.parseInt(port));
        } catch (UnknownHostException | NumberFormatException var4) {
            LOG.error("Error during jgroups initialization: ", var4);
        }

    }

    private boolean isTcpConfigurationUsed(String configuration) {
        return StringUtils.endsWith(configuration, "jgroups-tcp.xml") || StringUtils.endsWith(configuration, "jgroups-tcp.xml");
    }

    private boolean isJdbcPingUsed(String configuration) {
        return StringUtils.endsWith(configuration, "jgroups-tcp.xml");
    }

    private void setDatabaseSystemProperties() {
        HybrisDataSource dataSource = Registry.getCurrentTenant().getDataSource();
        if (this.isJndiDataSource()) {
            System.setProperty("hybris.datasource.jndi.name", dataSource.getJNDIName());
            LOG.debug("--- setting up hybris.datasource.jndi.name to: {}", dataSource.getJNDIName());
        } else {
            Map<String, String> connParams = dataSource.getConnectionParameters();
            System.setProperty("hybris.database.driver", (String)connParams.get(Config.SystemSpecificParams.DB_DRIVER));
            System.setProperty("hybris.database.user", (String)connParams.get(Config.SystemSpecificParams.DB_USERNAME));
            System.setProperty("hybris.database.password", (String)connParams.get(Config.SystemSpecificParams.DB_PASSWORD));
            System.setProperty("hybris.database.url", (String)connParams.get(Config.SystemSpecificParams.DB_URL));
            LOG.debug("--- setting up hybris.database.driver to: {}", connParams.get(Config.SystemSpecificParams.DB_DRIVER));
            LOG.debug("--- setting up hybris.database.user to: {}", connParams.get(Config.SystemSpecificParams.DB_USERNAME));
            LOG.debug("--- setting up hybris.database.password to: {}", connParams.get(Config.SystemSpecificParams.DB_PASSWORD));
            LOG.debug("--- setting up hybris.database.url to: {}", connParams.get(Config.SystemSpecificParams.DB_URL));
        }

        String schemaInitDDL = this.createSchemaInitDDL();
        System.setProperty("hybris.jgroups.schema", schemaInitDDL);
        LOG.debug("--- setting up hybris.jgroups.schema to: {}", schemaInitDDL);
        String clearTable = this.getConfigValue("cluster.broadcast.method.jgroups.tcp.jdbcping.clear_table_on_view_change", "false");
        System.setProperty("hybris.jgroups.clear_table_on_view_change", clearTable);
        LOG.debug("--- setting up hybris.jgroups.clear_table_on_view_change to: {}", clearTable);
    }

    private boolean isJndiDataSource() {
        return StringUtils.isNotBlank(this.getConfigValue(Config.SystemSpecificParams.DB_POOL_FROMJNDI, (String)null));
    }

    private String createSchemaInitDDL() {
        return "CREATE TABLE JGROUPSPING (own_addr varchar(200) NOT NULL, cluster_name varchar(200) NOT NULL, ping_data " + (Config.isOracleUsed() ? "blob" : "varbinary(5000)") + " DEFAULT NULL, " + "PRIMARY KEY (own_addr, cluster_name) )";
    }

    private JChannel openChannel(String confgiuration) {
        JChannel jChannel = null;

        try {
            jChannel = new JChannel(confgiuration);
            jChannel.setName("hybrisnode-" + Registry.getClusterID());
            String channelName = this.getConfigValue("cluster.broadcast.method.jgroups.channel.name", "hybris-broadcast");
            jChannel.connect(channelName);
            jChannel.setReceiver(new ReceiverAdapter() {
                public void viewAccepted(View view) {
                    super.viewAccepted(view);
                }

                public void receive(Message msg) {
                    JGroups4BroadcastMethod.this.processMessage(msg);
                }
            });
        } catch (Exception var4) {
            LOG.error("Error during jgroups initialization: ", var4);
        }

        return jChannel;
    }

    private String getJgroupsConfiguration() {
        String configFile = this.getConfigFileName();
        LOG.debug("JGroups configuration has been chosen (file: {})", configFile);
        return "jgroups" + File.separator + configFile;
    }

    private String getConfigFileName() {
        return this.getConfigValue("cluster.broadcast.method.jgroups.configuration", "jgroups-udp.xml");
    }

    private String getConfigValue(String key, String defaultValue) {
        ConfigIntf config = Registry.getCurrentTenant().getConfig();
        return config.getString(key, defaultValue == null ? "" : defaultValue);
    }

    protected void processMessage(Message messageFromChannel) {
        RawMessage msg = new RawMessage(messageFromChannel.getBuffer(), messageFromChannel.getOffset(), messageFromChannel.getLength());
        DefaultBroadcastService dbs = (DefaultBroadcastService)this.service;
        if (dbs.accept(msg)) {
            this.notifyMessgageReceived(msg);
        }

    }
}
