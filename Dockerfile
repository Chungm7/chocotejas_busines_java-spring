FROM ubuntu:latest
LABEL authors="chung"

ENTRYPOINT ["top", "-b"]