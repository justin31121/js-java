::java -cp ".\libs\*;./" tests.GetTest
::java -cp ".\libs\*;.\*" tests.ReqTest &&
::java -cp ".\libs\*;.\*" tests.MaybeTest
java -cp ".\libs\*;./" tests.ServerTest

