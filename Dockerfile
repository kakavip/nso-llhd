FROM alpine

WORKDIR /work

RUN apk update && apk add openjdk8 && apk add apache-ant

ADD . .

RUN ant

EXPOSE 14444

CMD [ "ant", "run" ]