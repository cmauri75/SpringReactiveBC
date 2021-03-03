# Deploy customer using minikube
## What is minicube?
Minikube is a utility you can use to run Kubernetes (k8s) on your local machine. It creates a single node cluster contained in a virtual machine (VM). This cluster lets you demo Kubernetes operations without requiring the time and resource-consuming installation of full-blown K8s.


### Executions

* Starts minikube
* Share local docker daemon with minicube
* build the project docker image
* open minikube dashboard in browser
* Creates a pod containing rwa image
* expose port
```bash
minikube start
eval $(minikube docker-env)
docker images
mvn spring-boot:build-image

minikube dashboard

kubectl run rwa --image=rwa:0.0.1-SNAPSHOT  --image-pull-policy=Never
kubectl port-forward rwa 8080:8080


kubectl get deployments
kubectl get pods

```
