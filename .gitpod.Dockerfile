FROM gitpod/workspace-full:latest

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh \
             && sdk install java 25-tem \
             && sdk default java 25-tem"
