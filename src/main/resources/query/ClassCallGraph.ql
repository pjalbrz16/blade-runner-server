/**
 * @name Class call graph (methods + constructors)
 * @kind graph
 */

import java

query predicate nodes(ClassOrInterface c, string attr, string val) {
  c.isSourceDeclaration() and
  attr = "label" and val = c.getQualifiedName()
}

query predicate edges(ClassOrInterface fromC, ClassOrInterface toC, string attr, string val) {

  // Method calls
  exists(MethodCall call |
    fromC = call.getEnclosingCallable().getDeclaringType().(ClassOrInterface) and
    toC   = call.getCallee().getDeclaringType().(ClassOrInterface) and
    fromC != toC and
    attr = "label" and val = "calls"
  )

  or

  // Constructor calls: new ToC(...)
  exists(ClassInstanceExpr ci |
    fromC = ci.getEnclosingCallable().getDeclaringType().(ClassOrInterface) and
    toC   = ci.getConstructor().getDeclaringType().(ClassOrInterface) and
    fromC != toC and
    attr = "label" and val = "news"
  )
}
