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

Other possibility is using kind
Install and create cluster, first install go 1.17+, than:
```
go install sigs.k8s.io/kind@v0.14.0 && kind create cluster
```
Creare customer cluster and check
```
kind delete cluster --name reactive-cluster
kind create cluster --config kindConfig.yaml
kind get clusters
```

Load images in cluster and check:
```
kind load docker-image customer:0.0.1-SNAPSHOT orders:0.0.1-SNAPSHOT  gateway:0.0.1-SNAPSHOT
docker exec -it kind-control-plane crictl images
```

Deploy an nginx ingress controller in our cluster
```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```

kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission
kubectl apply -f customerService.yaml
kubectl get services


