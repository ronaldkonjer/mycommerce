pipeline {
  agent {
    label "jenkins-gradle"
  }
  environment {
    ORG               = 'hybris-jenkins-x-demo'
    APP_NAME          = 'mycommerce'
    CHARTMUSEUM_CREDS = credentials('jenkins-x-chartmuseum')
  }
  stages {
    stage("Bootstrap") {
      steps {
        container('gradle') {
          sh 'gradle bootstrapPlatform -PnexusUrl="http://nexus/repository/maven-releases"'
        }
      }
    }

    stage("Rebuild") {
      steps {
        container('gradle') {
          sh 'gradle yclean yall'
        }
      }
    }

    stage("Tests") {
      when {
        not {
          branch 'master'
        }
      }
      steps {
        container('gradle') {
          sh 'gradle unittests'
        }
      }
      post {
        always {
          junit 'hybris/log/junit/*.xml'
        }
      }
    }

    stage("Sonar") {
      when {
        not {
          branch 'master'
        }
      }
      steps {
        container('gradle') {
          sh 'gradle sonar -PsonarURL=http://sonar-sonarqube:9000'
        }
      }
    }

    stage('Build Release') {
      when {
        branch 'master'
      }
      steps {
        container('gradle') {
          // ensure we're not on a detached head
          sh "git checkout master"
          sh "git config --global credential.helper store"
          sh "jx step validate --min-jx-version 1.1.73"
          sh "jx step git credentials"
          // so we can retrieve the version in later steps
          sh "echo \$(jx-release-version) > VERSION"
          // TODO
          //sh "mvn versions:set -DnewVersion=\$(cat VERSION)"

          sh 'gradle prepareDocker'
          sh 'export VERSION=`cat VERSION` && skaffold run -f skaffold.yaml || true'

          dir ('./charts/mycommerce') {
            sh "make tag"
          }

          sh "jx step validate --min-jx-version 1.2.36"
          sh "jx step post build --image \$JENKINS_X_DOCKER_REGISTRY_SERVICE_HOST:\$JENKINS_X_DOCKER_REGISTRY_SERVICE_PORT/$ORG/$APP_NAME:\$(cat VERSION)"
        }
      }
    }
    stage('Promote to Environments') {
      when {
        branch 'master'
      }
      steps {
        dir ('./charts/mycommerce') {
          container('gradle') {
            sh 'jx step changelog --version v\$(cat ../../VERSION)'

            // release the helm chart
            sh 'make release'

            // promote through all 'Auto' promotion Environments
            sh 'jx promote -b --all-auto --timeout 1h --version \$(cat ../../VERSION)'
          }
        }
      }
    }
  }
  post {
      always {
           cleanWs()
      }
  }
}
