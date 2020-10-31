#!/usr/bin/env bash

#
# First install gcloud cloud-shell from https://cloud.google.com/sdk/gcloud/reference/alpha/cloud-shell/ssh
#
# Uncomment if you're not already logged in:
# gcloud auth login
#
gcloud config set project fiery-bonbon-292721
gcloud container clusters get-credentials spring-boot-cluster --zone us-east4-a --project fiery-bonbon-292721
#
# The following command will generate an ssh key the first time you run it
#
# gcloud alpha cloud-shell ssh
#
# The following commands need to be run in the Google Cloud shell

# Create a simple deployment
kubectl create deployment spring-boot --image=kenfreeman/spring-boot

# Create a simple service (just 1 pod)
kubectl expose deployment spring-boot --type=LoadBalancer --port=80 --target-port=8888

kubectl get pods -o wide
kubectl get rs -o wide
kubectl get events

# ssh in
kubectl get pods # get pod name

kubectl exec --stdin --tty spring-boot-78d5597d96-nqphr -- /bin/sh
