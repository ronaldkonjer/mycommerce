CHART_REPO := http://jenkins-x-chartmuseum:8080
CURRENT=$(pwd)
NAME := mycommerce
ORG := hybris-jenkins-x-demo
OS := $(shell uname)
RELEASE_VERSION := $(shell cat ../../VERSION)

build: clean
	rm -rf requirements.lock
	helm dependency build
	helm lint

clean:
	# rm -rf charts
	rm -rf ${NAME}*.tgz

release: clean
	helm dependency build
	helm lint
	helm init --client-only
	helm package .
	curl --fail -u $(CHARTMUSEUM_CREDS_USR):$(CHARTMUSEUM_CREDS_PSW) --data-binary "@$(NAME)-$(shell sed -n 's/^version: //p' Chart.yaml).tgz" $(CHART_REPO)/api/charts
	rm -rf ${NAME}*.tgz%

tag:
ifeq ($(OS),Darwin)
	sed -i "" -e "s/version:.*/version: $(RELEASE_VERSION)/" Chart.yaml
	sed -i "" -e "s/tag: .*/tag: $(RELEASE_VERSION)/" values.yaml
else ifeq ($(OS),Linux)
	sed -i -e "s/version:.*/version: $(RELEASE_VERSION)/" Chart.yaml
	sed -i -e "s/version:.*/version: $(RELEASE_VERSION)/" charts/ysolr/Chart.yaml
	sed -i -e "s/repository: .*/repository: $(JENKINS_X_DOCKER_REGISTRY_SERVICE_HOST):$(JENKINS_X_DOCKER_REGISTRY_SERVICE_PORT)\/$(ORG)\/$(NAME)/" values.yaml
	sed -i -e "s/tag: .*/tag: $(RELEASE_VERSION)/" values.yaml
	sed -i -e "s/repository: .*/repository: $(JENKINS_X_DOCKER_REGISTRY_SERVICE_HOST):$(JENKINS_X_DOCKER_REGISTRY_SERVICE_PORT)\/$(ORG)\/$(NAME)-ysolr/" charts/ysolr/values.yaml
	sed -i -e "s/tag: .*/tag: $(RELEASE_VERSION)/" charts/ysolr/values.yaml
else
	echo "platfrom $(OS) not supported to release from"
	exit -1
endif
	git add --all
	git commit -m "release $(RELEASE_VERSION)" --allow-empty # if first release then no verion update is performed
	git tag -fa v$(RELEASE_VERSION) -m "Release version $(RELEASE_VERSION)"
	git push origin v$(RELEASE_VERSION)
