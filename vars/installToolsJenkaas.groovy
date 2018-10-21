/* Installs required tools for testing
 *
 * We have to use python3.5 for the setup as xenial is the base OS on jenkins slaves
 */
def call() {
    sh "sudo add-apt-repository -y ppa:deadsnakes/ppa"
    sh "sudo apt update"
    sh "sudo apt install -qyf libffi-dev python3.6 python3.6-dev python3-dev libffi-dev libssl-dev libxml2-dev libxslt1-dev libjpeg8-dev zlib1g-dev"
    sh "sudo -H -E python3.5 -m pip install -U pipenv"
    sh "sudo -H -E python3.6 -m pip install -U pipenv"
    sh "cd jobs && python3.5 -m pipenv --python 3.5 install --skip-lock"
    sh "cd jobs && python3.6 -m pipenv --python 3.6 install --skip-lock"

    // Charmstore auth
    withCredentials([file(credentialsId: 'charm_creds', variable: 'CHARMCREDS'),
                     file(credentialsId: 'juju_creds', variable: 'JUJUCREDS'),
                     file(credentialsId: 'snapcraft_creds', variable: 'SNAPCRAFTCREDS')]) {

        sh "cd jobs/infra && pipenv --python 3.5 run ansible-playbook playbook-jenkins.yml --extra-vars 'charm_creds=${CHARMCREDS} juju_creds=${JUJUCREDS} snapcraft_creds=${SNAPCRAFTCREDS}'"
    }
}
