# cs427group20-simple-slack-api-tests

# Setup
- Clone https://github.com/moults31/simple-slack-api and build its jar. To do this, cd into the repo and run `./gradlew jar -i`
- Copy simple-slack-api/sources/build/simpleslackapi-1.3.0.jar into  s427group20-simple-slack-api-tests/libs/simpleslackapi-1.3.0.jar
- Create resources/env.sh and copy the following text into it: 
```
export CS427GROUP20_SLACK_BOT_AUTHTOKEN="xoxb-<token>"
export CS427GROUP20_SLACK_APP_AUTHTOKEN="xapp-<token>"
```
 replacing <token> with the cs427group20-slack-client bot and user oauth tokens.
- Make sure you have jdk 8 installed and JAVA_HOME set accordingly

# Build 
`./gradlew fatJar -i`

# Run
- Do `source resources/env.sh` to export the tokens as environment variables in the shell you will run in
- `java -jar build/libs/cs427group20-simple-slack-api-tests-1.0-SNAPSHOT.jar`

