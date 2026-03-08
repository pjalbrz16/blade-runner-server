/**
 * @name Spring bean interactions
 * @description Find all calls from one Spring bean to another.
 * @kind graph
 * @id java/spring/bean-interactions
 */

import java

/**
 * A class that is considered a Spring bean.
 * This includes classes annotated with @Component, @Service, @Repository, @Controller, @RestController, @Configuration, @ConfigurationProperties.
 * It also includes classes with methods annotated with @Bean.
 */
class SpringBean extends ClassOrInterface {
  SpringBean() {
    this.getAnAnnotation().getType().getQualifiedName() in [
      "org.springframework.stereotype.Component",
      "org.springframework.stereotype.Service",
      "org.springframework.stereotype.Repository",
      "org.springframework.stereotype.Controller",
      "org.springframework.web.bind.annotation.RestController",
      "org.springframework.context.annotation.Configuration",
      "org.springframework.boot.context.properties.ConfigurationProperties"
    ]
    or
    // Check if it's produced by a @Bean method
    exists(Method m |
      m.getAnAnnotation().getType().getQualifiedName() = "org.springframework.context.annotation.Bean" and
      (
        m.getType() = this
        or
        this.getAnAncestor() = m.getType()
      )
    )
  }
}

/**
 * Represents a call from one bean to another.
 */
query predicate nodes(ClassOrInterface b, string attr, string val) {
  b instanceof SpringBean and
  attr = "label" and val = b.getQualifiedName()
}

query predicate edges(ClassOrInterface fromB, ClassOrInterface toB, string attr, string val) {
  fromB instanceof SpringBean and
  toB instanceof SpringBean and
  fromB != toB and
  (
    exists(MethodCall call |
      fromB = call.getEnclosingCallable().getDeclaringType() and
      toB   = call.getCallee().getDeclaringType() and
      attr = "label" and val = "calls"
    )
    or
    // Handle calls to methods defined in an interface/superclass that is a bean
    exists(MethodCall call |
      fromB = call.getEnclosingCallable().getDeclaringType() and
      exists(ClassOrInterface base |
        base = call.getCallee().getDeclaringType().getAnAncestor() and
        base instanceof SpringBean and
        toB = base
      ) and
      attr = "label" and val = "calls"
    )
    or
    // Handle calls to implementations where the interface is a bean
    exists(MethodCall call |
      fromB = call.getEnclosingCallable().getDeclaringType() and
      exists(ClassOrInterface impl |
        impl.getAnAncestor() = call.getCallee().getDeclaringType() and
        impl instanceof SpringBean and
        toB = impl
      ) and
      attr = "label" and val = "calls"
    )
    or
    // Handle constructor calls (e.g. for bean creation or dependency injection if manual)
    exists(ClassInstanceExpr ci |
      fromB = ci.getEnclosingCallable().getDeclaringType() and
      ci.getConstructor().getDeclaringType() = toB and
      attr = "label" and val = "news"
    )
  )
}
