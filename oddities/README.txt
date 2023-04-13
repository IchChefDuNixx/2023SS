This program will access synapse's files through your account where necessary. For this to work, it needs
- your spellstone USER_ID (not username or kong id or kong name)
- your password in an encoded format (not plaintext)

You can find them like this:
1. Open your browser of choice (tested with chrome and edge)
2. Open the developer tools/console. Either by pressing F12 or in your 3 dots menu
3. Select the "Network" tab and make sure "Disable cache" is checked (just in case)
4. With the dev tools still open, go to https://www.kongregate.com/games/synapticon/spellstone
    (log into kong if necessary) and wait until the game is fully loaded
5. Now you need to find the row where it says "api.php?message=init", Try filtering!
    - Select Fetch/XHR
    - search by the name directly (edge/chrome have 2 fields for that, both are valid)
6. Click on it and new information should pop up from the side. This is where you go to the second tab "Payload" (between "Headers" and "Preview")
    - Here I can see 2 sections, one is very small and the other ~20 lines long
7. Find your numerical user_id and encoded password (around the top)

Please provide these credentials during your first execution of flask_extractor.exe
and they will be saved for future use (I, IchChefDuNixx, do never have access to any of them!)
In case you typo'd or got other issues, just delete the credentials.json file and restart flask_extractor

Also make sure there are no leading or trailing spaces as I didn't take care of that functionality yet.
It should look something like this:

{'user_id': '0123456789', 'password': 'abcdefghijklmnopqrstuvwxyz123456}
in my case, 10 digits and 30 characters respectively

And NOT this:
{'user_id': 'IchChefDuNixx', 'password': ' abc'}

If everything worked so far, just follow the command line prompts to see the results.
Don't forget to scroll down and/or right to see the full table if necessary!