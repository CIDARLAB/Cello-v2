FROM adoptopenjdk/openjdk8:debian

VOLUME /tmp

ARG YOSYS_VERSION
ARG JAR_FILE

RUN apt-get update
RUN apt-get install -y --no-install-recommends bison clang flex gawk gcc-multilib git graphviz libffi-dev libffi6 libreadline-dev libreadline7 pkg-config python3 tcl tcl-dev

RUN	cd /root && \
	curl -s -L https://github.com/YosysHQ/yosys/archive/yosys-$YOSYS_VERSION.tar.gz --output yosys-$YOSYS_VERSION.tar.gz && \
	tar xzf yosys-$YOSYS_VERSION.tar.gz && \
	cd yosys-yosys-$YOSYS_VERSION && \
	make && \
	make install

RUN cd /root && \
	# curl -s -L https://gitlab.cecs.anu.edu.au/u5568237/cf2cs/raw/0f758d7b06c597e30db0579695107aadff0db486/hmetis-1.5-linux/hmetis-1.5-linux.tar.gz --output hmetis-1.5-linux.tar.gz && \
	curl -s -L http://glaros.dtc.umn.edu/gkhome/fetch/sw/hmetis/hmetis-1.5-linux.tar.gz --output hmetis-1.5-linux.tar.gz && \
	tar xzf hmetis-1.5-linux.tar.gz && \
	cd hmetis-1.5-linux && \
	install hmetis /usr/bin

RUN apt-get remove -y bison clang curl flex gawk git libffi-dev libreadline-dev pkg-config tcl-dev

COPY target/${JAR_FILE} /root/app.jar
