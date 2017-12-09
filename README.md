# github bot
A bot to help us with tedious github tasks.

## Running
There are standard gradle tasks:
- `assemble`: builds the code
- `build`: builds and tests the code
- `run`: assembles and executes the application
- `test`
- `clean`

For continuous rebuilds, which watches for any changes to `src/`, we recommend:
```sh
./gradlew -t run
```

This will run the bot, by default, on http://localhost:8080.

---

To mimic a heroku environment, use:
```sh
./gradlew stage # build
heroku local web # run
```

This will run the bot, by default, on http://localhost:5000. However, I do not
know how to run continuous builds this way.

## Deployment
We deploy on heroku:
```sh
git push heroku master
```
