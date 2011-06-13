@echo off
REM The gameplan for each svn stat report:
REM 1. by date
REM 2. grepping for certain svn commit tags
REM 3. excluding jar files

SET START_DATE=2011-02-01
SET END_DATE=2011-05-01
SET OUTPUT_NAME=mystats_%END_DATE%
SET WORKSPACE_DIR=c:\Projects\myApp\trunk\

REM USAGE: GREP_STRING  (Optional)
REM   You can put "ContactPage" to grep all ContactPage related comments
REM   You can also put "-v ContactPage" to grep all NOT ContactPage
SET GREP_STRING=


@echo on
svn log --xml -vr{%START_DATE%}:{%END_DATE%} %WORKSPACE_DIR% > %OUTPUT_NAME%.log

@echo off
if "%GREP_STRING%" == "" goto JAR
@echo on
groovy GrepSvnLogForCommitTag.groovy %OUTPUT_NAME%.log grep %GREP_STRING%

:JAR
@echo on
java -jar statsvn.jar -exclude **\*.jar -output-dir .\%OUTPUT_NAME% %OUTPUT_NAME%.log %WORKSPACE_DIR%
