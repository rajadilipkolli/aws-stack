FROM gitpod/workspace-full:latest

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh \
             && sdk install java 22.3.2.r19-grl \
             && sdk default java 22.3.2.r19-grl"
