#!/usr/bin/env bash

#
# First install gcloud cloud-shell from https://cloud.google.com/sdk/gcloud/reference/alpha/cloud-shell/ssh
# And enable your project for API access: https://console.cloud.google.com/apis/library/container.googleapis.com?q=kubernetes%20engine&_ga=2.81577365.346290891.1604335259-285803584.1602094460
#
# Uncomment if you're not already logged in:
# gcloud auth login

# To logout and use minikube: gcloud auth revoke --all followed by minikube start
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

# Update deployment - always change grace period seconds to force an update
kubectl patch deployment spring-boot -p \
  '{"spec":{"template":{"spec":{"terminationGracePeriodSeconds":31}}}}'

# or
kubectl rollout restart deployment/frontend

kubectl set image deployment/spring-boot spring-boot=kenfreeman/spring-boot --record

# Create a simple service (just 1 pod)
kubectl expose deployment spring-boot --type=LoadBalancer --port=80 --target-port=8888

kubectl get pods -o wide
kubectl get rs -o wide
kubectl get events

# ssh in
kubectl get pods # get pod name

kubectl exec --stdin --tty spring-boot-78d5597d96-nqphr -- /bin/sh # Replace with pod name from above

# getting logs

kubectl logs <podname>
