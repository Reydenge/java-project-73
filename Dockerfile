FROM gradle:7.6-jdk17

WORKDIR /

COPY . /

RUN gradle installDist

CMD build/install/app/bin/app