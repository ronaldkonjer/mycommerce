{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "ycommerce.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "ycommerce.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{- define "ycommerce.backend" -}}
{{- $fullname := include "ycommerce.fullname" . -}}
{{- printf "%s-%s" $fullname "backend" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "ycommerce.frontend" -}}
{{- $fullname := (include "ycommerce.fullname" .) -}}
{{- printf "%s-%s" $fullname "frontend" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "ycommerce.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "ycommerce.backend.pods" -}}
{{ range $k, $v := until (int .Values.backend.replicaCount) }}
{{- printf "- %s-%d\n" (include "ycommerce.fullname" $) $v -}}
{{- end -}}
{{- end -}}

{{- define "skaffold.or.default" -}}
{{- default (printf "%s:%s" .Values.image.repository .Values.image.tag ) .Values.imageOverride -}}
{{- end -}}

{{- /*
Alle y_... properties und was sonsnt nonch dazu gehört raus rendern
*/ -}}
{{- define "commerce.env" -}}
- name: HYBRIS_OPT_CONFIG_DIR
  value: /opt/hybris/opt-config-dir
- name: KUBE_NAMESPACE
  valueFrom:
    fieldRef:
       fieldPath: metadata.namespace
- name: KUBE_LABEL
  value: 'app={{ ( include "ycommerce.name" . )}},release={{ .Release.Name }},tier=application'
- name: y_solr_config_Default_mode
  value: "STANDALONE"
- name: y_solr_config_Default_urls
  value: "http://{{ printf "%s-ysolr" .Release.Name }}:8983/solr"  
- name: GOOGLE_APPLICATION_CREDENTIALS
  value: /secrets/cloudsql/mysql-sa.json 
{{ .Values.commonenv | toYaml}}
{{- end -}}