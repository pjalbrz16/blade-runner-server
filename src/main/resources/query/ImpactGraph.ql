/**
 * @name PR Impact Graph
 * @kind path-problem
 * @problem.severity recommendation
 * @id java/pr-impact-visualizer
 */

import java
import semmle.code.java.dataflow.DataFlow

// Define what we consider "The PR"
predicate isChangedFile(File f) {
  f.getAbsolutePath().matches("%/PATH/TO/YOUR/FILE/FROM/DIFF%")
}

from MethodAccess call, Method target, Method caller
where
  call.getMethod() = target and
  caller = call.getEnclosingCallable() and
  isChangedFile(target.getFile()) and // Target is in the PR
  not isChangedFile(caller.getFile())  // Caller is OUTSIDE the PR (the interaction)
select call, caller, target, "External interaction with PR code"