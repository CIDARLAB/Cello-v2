FROM openjdk:8-jdk-alpine

VOLUME /tmp

ARG YOSYS_VERSION
ARG JAR_FILE

RUN apk add --update --no-cache mongodb graphviz curl readline-dev readline alpine-sdk bash clang gawk bison flex tcl-dev tcl libffi-dev libffi pkgconfig python3 && \
	cd /root && \
	curl -L https://github.com/YosysHQ/yosys/archive/yosys-$YOSYS_VERSION.tar.gz --output yosys-$YOSYS_VERSION.tar.gz && \
	tar xvzf yosys-$YOSYS_VERSION.tar.gz && \
	cd yosys-yosys-$YOSYS_VERSION && \
	make && \
	make install && \
	apk del curl readline-dev alpine-sdk clang gawk bison flex tcl-dev libffi-dev pkgconfig

COPY target/${JAR_FILE} /root/app.jar
