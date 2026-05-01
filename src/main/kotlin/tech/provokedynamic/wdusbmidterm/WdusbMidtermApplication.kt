package tech.provokedynamic.wdusbmidterm

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration(
    exclude = [
        JmxAutoConfiguration::class,
        TaskSchedulingAutoConfiguration::class,
        TaskExecutionAutoConfiguration::class,
    ]
)
@ComponentScan(
    basePackages = [
        "tech.provokedynamic.wdusbmidterm.service",
        "tech.provokedynamic.wdusbmidterm.repository",
        "tech.provokedynamic.wdusbmidterm.controller",
    ]
)
@EntityScan("tech.provokedynamic.wdusbmidterm.entity")
class WdusbMidtermApplication

fun main(args: Array<String>) {
    runApplication<WdusbMidtermApplication>(*args)
}
