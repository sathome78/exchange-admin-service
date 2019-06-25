// Active Choices plugin required
properties([
    parameters([
        [$class: 'ChoiceParameter', 
            choiceType: 'PT_SINGLE_SELECT', 
            description: 'Select Deployment Env from the Dropdown List', 
            filterLength: 1, 
            filterable: true, 
            name: 'deploy_env', 
            randomName: 'choice-parameter-admin-service-deploy-env', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: [
                    classpath: [], 
                    sandbox: false,
                    script: 
                        'return[\'Could not get deploy_env\']'
                ], 
                script: [
                    classpath: [], 
                    sandbox: false,
                    script: 
                        'return["devtest","uat","prod"]'
                ]
            ]
        ], 
        [$class: 'CascadeChoiceParameter', 
            choiceType: 'PT_SINGLE_SELECT', 
            description: 'Select kube config from the Dropdown List', 
            filterLength: 1, 
            filterable: false, 
            name: 'kubeconfigMap', 
            randomName: 'choice-parameter-admin-service-kubeconfigMap', 
            referencedParameters: 'deploy_env', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: [
                    classpath: [], 
                    sandbox: false, 
                    script: 
                        'return[\'Could not get Environment from deploy_env Param\']'
                ], 
                script: [
                    classpath: [], 
                    sandbox: false, 
                    script: 
                        ''' if (deploy_env.equals("devtest")){
                                return["kube-config-exrates-k8s-name"]
                            }
                            else if(deploy_env.equals("uat")){
                                return["kube-config-exrates-k8s-name"]
                            }
                            else if(deploy_env.equals("prod")){
                                return["kube-config-exrates-k8s-name"]
                            }
                        '''
                ]
            ]
        ],
        [$class: 'CascadeChoiceParameter', 
            choiceType: 'PT_SINGLE_SELECT', 
            description: 'Run helm rollout if deployment failed', 
            filterLength: 1, 
            filterable: false, 
            name: 'ROLLOUT_IF_FAILED', 
            randomName: 'choice-parameter-admin-service-ROLLOUT_IF_FAILED', 
            referencedParameters: 'deploy_env', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: [
                    classpath: [], 
                    sandbox: false, 
                    script: 
                        'return[\'Could not get Environment from deploy_env Param\']'
                ], 
                script: [
                    classpath: [], 
                    sandbox: false, 
                    script: 
                        ''' if (deploy_env.equals("devtest")){
                                return["yes", "no"]
                            }
                            else if(deploy_env.equals("uat")){
                                return["yes", "no"]
                            }
                            else if(deploy_env.equals("prod")){
                                return["yes", "no"]
                            }
                        '''
                ]
            ]
        ],
        [$class: 'CascadeChoiceParameter', 
            choiceType: 'PT_SINGLE_SELECT', 
            description: 'Configure RBAC for Developers', 
            filterLength: 1, 
            filterable: false, 
            name: 'ConfigureDevRBAC', 
            randomName: 'choice-parameter-admin-service-ConfigureDevRBAC', 
            referencedParameters: 'deploy_env', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: [
                    classpath: [], 
                    sandbox: false, 
                    script: 
                        'return[\'Could not get Environment from deploy_env Param\']'
                ], 
                script: [
                    classpath: [], 
                    sandbox: false, 
                    script: 
                        ''' if (deploy_env.equals("devtest")){
                                return["no", "yes"]
                            }
                            else if(deploy_env.equals("uat")){
                                return["no"]
                            }
                            else if(deploy_env.equals("prod")){
                                return["no"]
                            }
                        '''
                ]
            ]
        ]
    ])
])

jenkins_pipeline_library_version = "master"
library "jenkins-pipeline-library@${jenkins_pipeline_library_version}"

def generated_label = "admin-service-${UUID.randomUUID().toString()}"
def service_name = "admin-service"
def kubeconfigMountPath = "/kubeconfig"
def podMemoryRequests = '3Gi'
def podCPURequests = '1'

def get_services(job) {
    if (job == 'all'){
      return ["admin-service"]
    } else if (job == 'pods_only'){
      return ["admin-service"]
    }else{
      return [job]
    }
}

def parallelStagesDynamicRolloutStatus = get_services("pods_only").collectEntries {
    ["${it}" : generateDynamicRolloutStage(it)]
}

def generateDynamicRolloutStage(service) {
    return {
        stage("${service}") {
                echo "check rollout status for ${service}"
                sh "kubectl rollout status --watch deployment/${service} --namespace $service_name-${params.deploy_env} --timeout=20m"
        }
    }
}

def get_current_replica_count(String namespace, String node) {
  result = sh returnStdout: true, script: "kubectl get deployments --namespace ${namespace} ${namespace}-${node} -o jsonpath='{ .spec.replicas }'"
  return result
}

def new_replica_count(String namespace, String node, desired_replica_count) {
  current_replica_count = get_current_replica_count("${namespace}", "${node}")

  if(current_replica_count.toInteger() > "${desired_replica_count}".toInteger()) { 
     return current_replica_count
  } else{
     return "${desired_replica_count}".toInteger() 
  }
}

def get_docker_image_tag(build_tag, existing_tag) {
    if("${build_tag}" != "") { 
      return "${build_tag}"
    } else if("${existing_tag}" != ""){ 
        return "${existing_tag}" 
    } else {
      return "error"
    }
}

def get_las_successful_build(String job_name) {
  lastSuccessfulBuild = Jenkins.instance.getItem("${job_name}").lastSuccessfulBuild.number
  return lastSuccessfulBuild
}

def generate_flyway_configmaps(service_repo_path, env, namespace, service_name) {
  // update flyway configmaps
    sh "kubectl create configmap flyway-${service_name}-${env} --namespace ${namespace} --from-file ${service_repo_path}/backend/src/main/resources/db/migration/ -o yaml --dry-run | kubectl apply -f -"
}

def get_k8s_job_status(job_name, namespace) {
    result = sh returnStdout: true, script: "kubectl get jobs ${job_name} --namespace ${namespace} -o jsonpath='{.status.succeeded}'"
    return result
}

pipeline {
  agent {
    kubernetes {
      cloud 'kubernetes'
      label generated_label
      defaultContainer 'docker-helm-kubectl'
      yaml dockerInPodwithKubeConfigMount(kubeconfigMap, kubeconfigMountPath, podMemoryRequests, podCPURequests)
    }
  }

  parameters {
      string(name: 'PIPELINE_BRANCH',
        defaultValue: "master",
        description: 'Specify brach to build'
      )

      string(name: 'SERVICE_IMAGE_TAG',
        defaultValue: "",
        description: 'Specify docker image tag or leave empty to build new one.'
      )

      choice(name: 'SKIP_SERVICE_FLYWAY_MIGRATIONS',
        choices: ['no', 'yes'],
        description: 'Skip flyway migration job?'
      )
  }

  environment {
    PIPELINE_BRANCH = "${params.PIPELINE_BRANCH}"
    SERVICE_NAME = "$service_name"

    TEMPLATES_DIR = "k8s.service/helm/basic_templates"

    KUBECONFIG = "$kubeconfigMountPath/${params.kubeconfigMap}"
    KUBERNETES_NAMESPACE = "$SERVICE_NAME-${params.deploy_env}"

    JENKINS_SLAVE_ECR_REGION = "us-east-2"

    SERVICE_REGION = "us-east-2"
    SERVICE_ACCOUNT_ID = "989806208174"
    SERVICE_BACKEND_ECR_URI = "$SERVICE_ACCOUNT_ID" + ".dkr.ecr." + "$SERVICE_REGION" + ".amazonaws.com/microservice-" + "$SERVICE_NAME" + "-backend-${params.deploy_env}"
    SERVICE_FRONTEND_ECR_URI = "$SERVICE_ACCOUNT_ID" + ".dkr.ecr." + "$SERVICE_REGION" + ".amazonaws.com/microservice-" + "$SERVICE_NAME" + "-frontend-${params.deploy_env}"

    CONFIGURE_K8S_RBAC = "${params.ConfigureDevRBAC}"
  }

  stages{
     stage('checkout repos and set job attributes') {
       parallel {
          stage('set build name and description') {
            when {
              expression { "${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != ""}
            }
            steps {
              // container('jnlp') {
                  script {
                    currentBuild.displayName = "$SERVICE_NAME-${params.deploy_env}-"+currentBuild.displayName
                    currentBuild.description = "$SERVICE_NAME build branch: $PIPELINE_BRANCH"
                  }
                  script {
                     env.BUILD_SERVICE_IMAGE_TAG=""
                  }
              // }
            }
          }
          stage('checkout service repo') {
            when {
              expression { "${params.SERVICE_IMAGE_TAG}" == "" && "${params.PIPELINE_BRANCH}" != "" }
            }
            steps {
              ansiColor('xterm') {
                  dir("$SERVICE_NAME") {
                    checkout([
                      $class: 'GitSCM',
                      branches: [[name: "$PIPELINE_BRANCH"]],
                      doGenerateSubmoduleConfigurations: false,
                      extensions: [
                         [$class: 'CloneOption',
                              depth: 1,
                              shallow: true
                         ]
                      ],
                      submoduleCfg: [],
                      userRemoteConfigs: [[credentialsId: 'jenkins-pipeline-ssh-key', url: 'ssh://git@bitbucket.to-the-moon-team-of-world-largest-exchange.com:8979/main/admin-service.git']]
                    ])
                
                    script {
                       env.SERVICE_REPO_PATH=sh(returnStdout: true, script: "pwd").trim()
                       env.SERVICE_GIT_COMMIT_HASH=sh(returnStdout: true, script: "git rev-parse HEAD").trim()
                       env.BUILD_SERVICE_IMAGE_TAG=""
                    }
                }
              }
            }
          }
          stage('checkout helm repo') {
            when {
              expression { "${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != ""}
            }
            steps {
              ansiColor('xterm') {
                  dir("helm") {
                    checkout([
                      $class: 'GitSCM',
                      branches: [[name: "master"]],
                      doGenerateSubmoduleConfigurations: false,
                      extensions: [
                         [$class: 'CloneOption',
                              depth: 1,
                              shallow: true
                         ]
                      ],
                      submoduleCfg: [],
                      userRemoteConfigs: [[credentialsId: 'jenkins-pipeline-ssh-key', url: 'ssh://git@bitbucket.to-the-moon-team-of-world-largest-exchange.com:8979/ops/exrates-ops-tools.git']]
                    ])
                  
                    script {
                       env.HELM_REPO_PATH=sh(returnStdout: true, script: "pwd").trim()
                    }
                  }
              }
            }
          }
      }
    }
    stage('build') {
      parallel {
          stage('build backend') {
            when {
              expression { "${params.SERVICE_IMAGE_TAG}" == "" && "${params.PIPELINE_BRANCH}" != "" }
            }
            steps {
              ansiColor('xterm') {
                  echo "build $SERVICE_NAME backend running..."
                  sh "cd ${SERVICE_REPO_PATH}/backend && docker build . -f ./backend-Dockerfile-build --tag $SERVICE_BACKEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER} --build-arg build_test_skip=true --build-arg APP_PORT=8080 --build-arg SERVICE_NAME=$SERVICE_NAME --build-arg SPRING_PROFILE=${params.deploy_env}"
                  sh "docker tag $SERVICE_BACKEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER} $SERVICE_BACKEND_ECR_URI:latest"
              }
            }
          }
          stage('build frontend') {
            when {
              expression { "${params.SERVICE_IMAGE_TAG}" == "" && "${params.PIPELINE_BRANCH}" != "" }
            }
            steps {
              ansiColor('xterm') {
                  echo "build $SERVICE_NAME frontend running..."
                  sh "cd ${SERVICE_REPO_PATH}/frontend && docker build . -f ./frontend-Dockerfile-build --tag $SERVICE_FRONTEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER} --build-arg build_test_skip=true --build-arg APP_PORT=8080 --build-arg SERVICE_NAME=$SERVICE_NAME --build-arg SPRING_PROFILE=${params.deploy_env}"
                  sh "docker tag $SERVICE_FRONTEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER} $SERVICE_FRONTEND_ECR_URI:latest"
              }
            }
          }
      }
    }
    stage('push images') {
      when {
        expression { "${params.SERVICE_IMAGE_TAG}" == "" && "${params.PIPELINE_BRANCH}" != ""}
      }
      parallel {
          stage('push backend image') {
            steps {
              ansiColor('xterm') {
                  echo "login to ECR"
                  sh "aws ecr get-login --region $SERVICE_REGION | sed -e 's/-e none//g' | bash"
                  echo "pushing service image..."
                  sh "docker push $SERVICE_BACKEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER}"
                  sh "docker push $SERVICE_BACKEND_ECR_URI:latest"
                  sh "docker rmi -f $SERVICE_BACKEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER}"
                  sh "docker rmi -f $SERVICE_BACKEND_ECR_URI:latest"
              }
              script {
                 env.BUILD_SERVICE_IMAGE_TAG="${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER}"
              }
            }
        }
        stage('push frontend image') {
            steps {
              ansiColor('xterm') {
                  echo "login to ECR"
                  sh "aws ecr get-login --region $SERVICE_REGION | sed -e 's/-e none//g' | bash"
                  echo "pushing service image..."
                  sh "docker push $SERVICE_FRONTEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER}"
                  sh "docker push $SERVICE_FRONTEND_ECR_URI:latest"
                  sh "docker rmi -f $SERVICE_FRONTEND_ECR_URI:${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER}"
                  sh "docker rmi -f $SERVICE_FRONTEND_ECR_URI:latest"
              }
              script {
                 env.BUILD_SERVICE_IMAGE_TAG="${SERVICE_GIT_COMMIT_HASH}-${BUILD_NUMBER}"
              }
            }
        }
      }
    }
    stage('kubectl config and test connection') {
      when {
        expression { "${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != ""}
      }
      steps {
        ansiColor('xterm') {
            echo "running kubectl connection test"
            echo "cat $KUBECONFIG > ~/.kube/config"
            sh "kubectl get pods --namespace $KUBERNETES_NAMESPACE"
        }
      }
    }
    stage('update flyway configmaps') {
      when {
        expression { "${params.PIPELINE_BRANCH}" != "" && "${params.SKIP_SERVICE_FLYWAY_MIGRATIONS}" == "no" }
      }
      steps {
        ansiColor('xterm') {
          script {
            generate_flyway_configmaps("${SERVICE_REPO_PATH}", "${params.deploy_env}", "${KUBERNETES_NAMESPACE}", "${SERVICE_NAME}")
          }
        }
      }
    }
    stage('generate helm charts') {
      parallel {
        stage('generate service-flyway helm chart') {
            steps {
                ansiColor('xterm') {
                    echo "Running ansible dry run"
                    sh "cd ${SERVICE_REPO_PATH} && ansible-playbook -c local -i ',localhost' -e 'service_playbook_dir=${HELM_REPO_PATH}/$TEMPLATES_DIR' -e 'SERVICE_NAME=${SERVICE_NAME}' -e 'deployment_env=$deploy_env' -e 'deployment_app_version=${BUILD_NUMBER}' -e 'deployment_flyway_job_name=flyway-dbupdater-job-${BUILD_NUMBER}' ansible-template-$deploy_env-env-flyway.yml --check"
                    echo "Running ansible to generate flyway helm chart"
                    sh "cd ${SERVICE_REPO_PATH} && ansible-playbook -c local -i ',localhost' -e 'service_playbook_dir=${HELM_REPO_PATH}/$TEMPLATES_DIR' -e 'SERVICE_NAME=${SERVICE_NAME}' -e 'deployment_env=$deploy_env' -e 'deployment_app_version=${BUILD_NUMBER}' -e 'deployment_flyway_job_name=flyway-dbupdater-job-${BUILD_NUMBER}' ansible-template-$deploy_env-env-flyway.yml"
                }
            }
        }
        stage('generate service helm charts') {
            steps {
                script {
                  env.BUILD_SERVICE_IMAGE_TAG=get_docker_image_tag("${BUILD_SERVICE_IMAGE_TAG}", "${params.SERVICE_IMAGE_TAG}")
                }
                
                ansiColor('xterm') {
                    echo "Running ansible dry run"
                    sh "cd ${SERVICE_REPO_PATH} && ansible-playbook -c local -i ',localhost' -e 'service_playbook_dir=${HELM_REPO_PATH}/$TEMPLATES_DIR' -e 'deployment_env=$deploy_env' -e 'deploy_image_tag=${BUILD_SERVICE_IMAGE_TAG}' -e 'deployment_app_version=${BUILD_NUMBER}' ansible-template-$deploy_env-env.yml --check"
                    echo "Running ansible to generate helm charts"
                    sh "cd ${SERVICE_REPO_PATH} &&ansible-playbook -c local -i ',localhost' -e 'service_playbook_dir=${HELM_REPO_PATH}/$TEMPLATES_DIR' -e 'deployment_env=$deploy_env' -e 'deploy_image_tag=${BUILD_SERVICE_IMAGE_TAG}' -e 'deployment_app_version=${BUILD_NUMBER}' ansible-template-$deploy_env-env.yml"
                }
            }
        }
      }
    }
    stage('helm init') {
        when {
          expression { "${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != ""}
        }
        steps {
            ansiColor('xterm') {
              echo "Running helm init"
              sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR && helm init --upgrade --client-only"
              echo "Updating $SERVICE_NAME helm dependencies"
              sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR && helm init --client-only && ./install.sh"
              echo "Updating flyway helm dependencies"
              sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR/flyway-chart && helm init --client-only && ./flyway_install.sh"
            }
        }
    }
    stage('helm dry-run') {
      when {
        expression { "${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != ""}
      }
      parallel {
        stage('helm service dry-run') {
            steps {
                ansiColor('xterm') {
                    echo "Running helm service dry-run deployment"
                    sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR && helm init --client-only && /usr/local/bin/helm upgrade --install --dry-run --debug $SERVICE_NAME --namespace $KUBERNETES_NAMESPACE  . "
                }
            }
        }
        stage('helm service-flyway dry-run') {
            steps {
                ansiColor('xterm') {
                    echo "Running helm flyway dry-run deployment"
                    sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR/flyway-chart && helm init --client-only && /usr/local/bin/helm upgrade --install --dry-run --debug $SERVICE_NAME-flyway --namespace $KUBERNETES_NAMESPACE  . "
                }
            }
        }
      }
    }
    stage('helm deploy service-flyway') {
        when {
          expression { "${params.SKIP_SERVICE_FLYWAY_MIGRATIONS}" == "no" }
        }
        steps {
            ansiColor('xterm') {
               echo "Running helm dry-run deployment"
               sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR/flyway-chart && helm init --client-only && /usr/local/bin/helm upgrade --install $SERVICE_NAME-flyway --namespace $KUBERNETES_NAMESPACE . "
            }
        }
    }
    stage('check flyway-dbupdater-job status') {
        when {
          expression { "${params.SKIP_SERVICE_FLYWAY_MIGRATIONS}" == "no" }
        }
        steps {
            ansiColor('xterm') {
               echo "Running helm dry-run deployment"
               sh "kubectl wait --for=condition=complete job/flyway-dbupdater-job-${BUILD_NUMBER} --namespace $KUBERNETES_NAMESPACE -o yaml --timeout=1h" //timeout - The length of time to wait before giving up. Zero means check once and don't wait, negative means wait for a week.
            }
            script {
               env.FLYWAY_JOB_STATUS=get_k8s_job_status("flyway-dbupdater-job-${BUILD_NUMBER}", "$KUBERNETES_NAMESPACE")
            }
            echo "FLYWAY_JOB_STATUS: ${FLYWAY_JOB_STATUS}"
        }
    }
    stage('helm deploy') {
      parallel {
        stage('delete succeeded flyway-dbupdater-job') {
            when {
                expression { env.FLYWAY_JOB_STATUS == "1" && "${params.SKIP_SERVICE_FLYWAY_MIGRATIONS}" == "no"  }
            }
            steps {
                ansiColor('xterm') {
                   echo "Running helm dry-run deployment"
                   sh "kubectl delete job --namespace $KUBERNETES_NAMESPACE flyway-dbupdater-job-${BUILD_NUMBER}"
                }
            }
        }
        stage('helm deploy service') {
            when {
                expression { env.FLYWAY_JOB_STATUS == "1" || "${params.SKIP_SERVICE_FLYWAY_MIGRATIONS}" == "yes" }
            }
            steps {
                ansiColor('xterm') {
                   echo "Running helm dry-run deployment"
                   sh "cd ${HELM_REPO_PATH}/$TEMPLATES_DIR && helm init --client-only && /usr/local/bin/helm upgrade --install $SERVICE_NAME --namespace $KUBERNETES_NAMESPACE . "
                }
                script {
                  currentBuild.description = currentBuild.description + ", image tag: ${BUILD_SERVICE_IMAGE_TAG}"
                }
            }
        }
      }
    }
    stage('configure RBAC') {
        when {
          expression { env.CONFIGURE_K8S_RBAC == "yes" && ("${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != "") }
        }
        steps {
            ansiColor('xterm') {
              // dir("helm") {
                echo "Add developer user to k8s using service account and create RBAC"
                sh "/${HELM_REPO_PATH}/$TEMPLATES_DIR/kubernetes_add_service_account_kubeconfig.sh sa-developer $KUBERNETES_NAMESPACE developer ${params.deploy_env}"
              // }
            }
        }
    }
    stage('rollout status') {
      parallel {
        stage('rollout status parallel stage') {
            when {
              expression { "${params.SERVICE_IMAGE_TAG}" != "" || "${params.PIPELINE_BRANCH}" != "" || "${params.ROLLOUT_IF_FAILED}" == "yes" }
            }
            steps {
                ansiColor('xterm') {
                  script {
                      parallel parallelStagesDynamicRolloutStatus
                  }
                }
            }
        }
      }
    }
  } //stages

}
