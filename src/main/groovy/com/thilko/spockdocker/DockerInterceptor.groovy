package com.thilko.spockdocker

import de.gesellix.docker.client.DockerAsyncCallback
import de.gesellix.docker.client.DockerClientImpl
import de.gesellix.docker.client.rawstream.RawInputStream
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation


class DockerInterceptor extends AbstractMethodInterceptor {

    Docker annotaton
    PortBinding port

    public DockerInterceptor(Docker annotation) {
        this.annotaton = annotation
        this.port = annotation.port()
    }

    @Override
    void interceptSpecExecution(IMethodInvocation invocation) throws Throwable {
        def dockerClient = new DockerClientImpl('unix:///var/run/docker.sock')
        def tag = "latest"
        def containerConfig = ["Cmd"         : [],
                               "ExposedPorts": ["${port.exposingPort()}": [:]],
                               "HostConfig"  : ["PortBindings": [
                                       "${port.exposingPort()}": [
                                               ["HostIp"  : port.hostIp(),
                                                "HostPort": port.hostPort()]]]]]

        def result = dockerClient.run(annotaton.image(), containerConfig, tag)

        def callback = new DockerAsyncCallback() {
            def lines = []

            @Override
            def onEvent(Object line) {
                lines << line
            }
        }

        dockerClient.logs(result.container.content.Id, [tail: 1], callback)

        while(!callback.lines.join("\n").contains(annotaton.readyString())){
            sleep(1000)
        }

        invocation.proceed()

        dockerClient.stop(result.container.content.Id)
        dockerClient.rm(result.container.content.Id)

    }


}
