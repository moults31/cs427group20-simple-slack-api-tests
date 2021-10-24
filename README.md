# cs427group20-simple-slack-api-tests

# Setup
- Clone https://github.com/moults31/simple-slack-api and build its jar. To do this, cd into the repo and run `./gradlew fatJar`
- Copy simple-slack-api/sources/build/simpleslackapi-1.3.0.jar into  s427group20-simple-slack-api-tests/libs/simpleslackapi-1.3.0.jar
- Create resources/env.sh and copy the following text into it: export CS427GROUP20_SLACK_BOT_AUTHTOKEN="<token>", replacing <token> with the cs427group20-slack-client oauth token.
- Make sure you have jdk 8 installed and JAVA_HOME set accordingly

# Build 
`./gradlew fatJar`

# Run
`java -jar build/libs/cs427group20-simple-slack-api-tests-1.0-SNAPSHOT.jar`
