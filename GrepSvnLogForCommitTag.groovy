/**
 * @author Paul Mazak
 */
if (args[0] == "help")
{
println """
Usage: groovy GrepSvnLogForCommitTag.groovy <svn.log> grep [-v] <regExp>
Example: groovy GrepSvnLogForCommitTag.groovy q_drm2.log grep "DRM"
	     (returns all from the log with commit tag containing 'DRM')
		 groovy GrepSvnLogForCommitTag.groovy q_drm2.log grep -v "Events"
		(returns all from the log with commit tag NOT containing 'Events')
"""
}
def svnLogFile = args[0]
def isNotOperator = false
def grepAnything = false
def regExp = ""
if (args.length > 1)
{
	grepAnything = true	
	isNotOperator = ("-v"==args[2])
	regExp = ""
	if (isNotOperator) {
		regExp = args[3]
	}
	else {
		regExp = args[2]
	}
}
def buf = ""
def keepBuf = ""
def wantToKeepLog = false
def inLog = false
def inMsg = false
def hasStartedLogEntries = false
new File(svnLogFile).eachLine {
	if (it.trim().startsWith("<logentry"))
	{
		hasStartedLogEntries = true
		inLog = true
	}
	if (!hasStartedLogEntries)
	{
		keepBuf += it+"\n"
	}
	else
	{
		buf += it+"\n"
	}
	if (inLog && it.trim().startsWith("</logentry"))
	{
		inLog = false
		inMsg = false
		if (wantToKeepLog)
		{
			keepBuf += buf
		}
		buf = ""
		wantToKeepLog = false
	}
	if (inLog && it.trim().startsWith("<msg>"))	
	{
		inMsg = true
	}
	if (inMsg)
	{
		if (!grepAnything ||
		   (!isNotOperator && it.contains(regExp)) || 
		   (isNotOperator && !it.contains(regExp)))
		{	
			wantToKeepLog = true
		}
	}
	if (inMsg && it.trim().startsWith("</msg>"))	
	{
		inMsg = false
	}
}
keepBuf += buf
def strippedFile = new File(svnLogFile)
strippedFile.text = keepBuf

/*
 * So that you know I wasn't crazy for doing it the above way...
 * The Following Groovy way of grepping did not work because StatSVN 
 * didn't like the XMLNodePrinted text as input.
 *
def logs = new XmlParser().parseText(new File(svnLogFile).text)
def writer = new FileWriter(svnLogFile)
def printer = new XmlNodePrinter(new PrintWriter(writer))
def strippedFileText = ""
logs.logentry.each{
	if (!grepAnything ||
	   (!isNotOperator && it.msg.text().contains(regExp)) || 
	   (isNotOperator && !it.msg.text().contains(regExp)))
	{		
		strippedFileText += it
	}
}
def strippedFile = new File(svnLogFile)
strippedFile.text = """<?xml version="1.0"?>
<log>
$strippedFileText
</log>
"""
*/