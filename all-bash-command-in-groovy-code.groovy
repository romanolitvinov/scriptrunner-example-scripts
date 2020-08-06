import org.apache.log4j.Logger
def proc = "bash --version".execute()
def b = new StringBuffer()
proc.consumeProcessErrorStream(b)
def log = Logger.getLogger("com.onresolve.scriptrunner.runner.ScriptRunnerImpl")

println proc.text
println b.toString()

def runCommand = { strList ->
  assert ( strList instanceof String ||
           ( strList instanceof List && strList.each{ it instanceof String } ) \
  )
  proc = strList.execute()
  proc.in.eachLine { line -> println line }
  proc.out.close()
  proc.waitFor()

  print "[INFO] ( "
  if(strList instanceof List) {
    strList.each { log.warn("Workflow function running... ${it}.") }
  } else {
    print strList
  }
  println " )"

  if (proc.exitValue()) {
    println "gave the following error: "
    println "[ERROR] ${proc.getErrorStream()}"
  }
  assert !proc.exitValue()
}
test_out = runCommand("echo 1")
// this one should work for you:
// def cmd = ['/bin/sh',  '-c',  'echo "${metric}" | nc carbon.hostedgraphite.com 2003']
def cmd1 = ['/bin/sh',  '-c',  'ls -la /tmp']
def cmd2 = ['/bin/sh',  '-c', 'uname -a']
def cmd3 = ['/bin/sh',  '-c',  'whoami']


cmd1.execute().with{
    def output = new StringWriter()
    def error = new StringWriter()
    //wait for process ended and catch stderr and stdout.
    it.waitForProcessOutput(output, error)
    //check there is no error
    println "error=$error"
    println "output=$output"
    println "code=${it.exitValue()}"
    log.warn("Workflow function running output=$output")

}

cmd2.execute().with{
    def output = new StringWriter()
    def error = new StringWriter()
    //wait for process ended and catch stderr and stdout.
    it.waitForProcessOutput(output, error)
    //check there is no error
    println "error=$error"
    println "output=$output"
    println "code=${it.exitValue()}"
    log.warn("Workflow function running output=$output")

}

cmd3.execute().with{
    def output = new StringWriter()
    def error = new StringWriter()
    //wait for process ended and catch stderr and stdout.
    it.waitForProcessOutput(output, error)
    //check there is no error
    println "error=$error"
    println "output=$output"
    println "code=${it.exitValue()}"
    log.warn("Workflow function running output=$output")

}

log.warn("Workflow function running... ${test_out}..")