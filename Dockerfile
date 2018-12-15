FROM amazonlinux

RUN yum install -y tar gunzip wget git java-1.8.0-openjdk-1.8.0.171

RUN bash -c "cd /usr/local/bin && curl -fsSLo boot https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh && chmod 755 boot"

RUN curl --silent --location https://dl.yarnpkg.com/rpm/yarn.repo | tee /etc/yum.repos.d/yarn.repo
RUN curl --silent --location https://rpm.nodesource.com/setup_10.x | bash -

RUN yum install -y yarn gcc-c++ make glibc-static
VOLUME /root/.m2
WORKDIR /lumo
ENTRYPOINT bash -c "BOOT_AS_ROOT=yes boot release"

