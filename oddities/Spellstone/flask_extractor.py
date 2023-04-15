#!/usr/bin/env python
# coding: utf-8

# 1. find flask ids in item_data
# 2. cross-reference these ids with user_items #skip
# 3. determine amount of flasks available
# 4. cross-reference choice id from flasks in item_data with item id in item_data #later?
# 5. determine amount of potential shards for each champion
# 6. get current shards for each champion owned
# 7. get level and necessary amount to upgrade each champion owned
# 8. determine which champions can be leveled up #and by using which flasks (done before)
# 9. make exceptions for lev (very optional)
# 10. turn this into a standalone .py and make it automated with pulling the data when loading kong
# Note: level 10 champs are not being shown in the final table, fix with lev update
# make 2 .exe: one for credential input and data update, another for reviewing the last result without updating, DONE: too easy

# %%
# imports

# built-in modules
import json
import os
import re
import warnings

# # external libraries
import pandas as pd
import readchar
import requests

from lxml import etree

# custom libraries, if applicable

# %%
def main(gui:bool=False):
    try:
        # console setup
        os.system(f"mode con cols={200} lines={1000}") 
        warnings.filterwarnings("ignore", category=FutureWarning)

        pd.set_option("display.max_columns", None)
        pd.set_option("display.width", 1000)
        pd.set_option("display.max_rows", None)
        
        # parse data into data structures
        user_items_raw = pd.read_json("user_items.json", orient="index")
        # print(user_items_raw.head())

        item_data_raw = pd.read_json("item_data.json", orient="index")
        # print(item_data_raw.head())

        user_units_raw = pd.read_json("user_units.json", orient="index")
        # print(user_units_raw.head())

        # use etree for cards_shard.xml
        cards_shard = etree.parse("cards_shard.xml") # type: ignore
        root = cards_shard.getroot()

        cards_shard_dict = {}
        for unit in root.findall("unit"):
            cards_shard_dict[int(unit.find("id").text)] = unit.find("name").text
    except Exception as e:
        print("\nError while processing local files occured!")
        input("Press ENTER to try fetching new data... (Else restart the flask_extractor.exe, check your files manually, check README.txt, then message IchChefDuNixx)")
        return
        
    try:
        # constants for id boundaries (04.04.23), easier to change here than in-line 
        flasks_id_min = 2534
        flasks_id_max = 2679

        # reward, aether, chaos, wyld
        champion_id_min = [4000, 5600, 6600, 7600]
        champion_id_max = [4999, 5999, 6999, 7999]

        # shard ids are auto-generated in-game as well
        champion_shards_id_min = []
        champion_shards_id_max = []
        for id_min, id_max in zip(champion_id_min, champion_id_max):
            champion_shards_id_min.append(id_min + 100000)
            champion_shards_id_max.append(id_max + 100000)

        # constant champ level requirements
        champion_costs = [50, 75, 100, 150, 225, 300, 400, 600, 900, 1200]
        champion_costs_mythic = [150, 200, 250, 300, 450, 600, 750, 900, 1100, 1300]


        # 1 find flask ids by regex (excluding mythical flasks manually)
        item_data_flask_ids = item_data_raw[item_data_raw["name"].str.contains("Flask")][4:] # .dropna(axis=1, how="all")
        flasks_id_min_temp = item_data_flask_ids["id"].min()
        flasks_id_max_temp = item_data_flask_ids["id"].max()

        # prevent reducing the id range
        if flasks_id_min_temp <= flasks_id_min and flasks_id_max_temp >= flasks_id_max:
            flasks_id_min = flasks_id_min_temp
            flasks_id_max = flasks_id_max_temp
        else:
            print("Error in #1: this action could possibly exclude flasks. Message IchChefDuNixx")
            input("Press ENTER to confirm the error and exit...")
            return
            
        # create new dataframe with flask id and respective reward ids 
        rows = []
        for index, row in item_data_flask_ids.iterrows():
            obj_id = row['id']
            obj_name = row["name"]
            reward_ids = []
            for choice in row['choice']:
                reward_ids.append(choice['item'][0]['id'])
            rows.append([obj_id, obj_name] + reward_ids)

        item_data_flask_rewards = pd.DataFrame(rows, columns=['id', "flask_name", 'reward1', 'reward2', 'reward3', 'reward4'])
        # item_data_flask_rewards.head()


        # 3 create new dataframe with flask id and amount owned
        user_items_flasks = user_items_raw[user_items_raw["id"].isin(item_data_flask_rewards["id"])].merge(item_data_flask_rewards)
        # user_items_flasks.head()


        # 5 create new dataframe with total amount of shards available per champion (very sql like)
        user_items_flasks_melted = user_items_flasks.melt(id_vars=['id', "number"], value_vars=['reward1', 'reward2', 'reward3', 'reward4'], var_name='reward_col', value_name='reward')
        # print(user_items_flasks_melted.head())

        # 
        user_items_flasks_grouped = user_items_flasks_melted.groupby(["reward"]).agg({"id":"count", "number":"sum"}).reset_index()
        # user_items_flasks_grouped.head()

        # create dict with flask ids which give a certain shard
        # {'107607': [2596], '104004': [2603, 2648], '104008': [2604, 2658, 2671],...}
        source_dict = {}
        for i, row in user_items_flasks_melted.iterrows():
            if row["reward"] is not None:
                if row["reward"] in source_dict:
                    source_dict[row["reward"]].append(row["id"])
                else:
                    source_dict[row["reward"]] = [row["id"]]
                    
        # create new dataframe from source_dict which this structure: 	            source_0	source_1	source_2
                                                                    #    107607	    2596	    NaN	        NaN
                                                                    #    104004	    2603	    2648.0	    NaN
                                                                    #    104008	    2604	    2658.0	    2671.0
        item_data_shard_source = pd.DataFrame.from_dict(source_dict, orient="index", columns=[f"source_{i}" for i in range(1,max([len(val) for val in source_dict.values()])+1)])

        # merge previous list with shard sums with shard source (EXLUDING YET UNBUILT CHAMPIONS)
        item_data_shards = user_items_flasks_grouped.merge(item_data_shard_source, left_on="reward", right_index=True)
        item_data_shards.rename(columns={"id":"num_flasks", "number":"flask_shards"}, inplace=True)
        item_data_shards["reward"] = item_data_shards["reward"].astype(int)
        # item_data_shards.head()


        # 6 create new dataframe with shard id and number of champion shards in inventory
        user_items_champion_shards = []

        # append multiple dataframes to lists
        for id_min, id_max in zip(champion_shards_id_min, champion_shards_id_max):
            user_items_champion_shards.append(user_items_raw[user_items_raw["id"].between(id_min, id_max)])

        # concat dataframes into one
        user_items_champion_shards = pd.concat(user_items_champion_shards)
        # user_items_champion_shards.head()


        # 7 create new dataframe with unit_id and level of all owned champions (un-crafted might not count yet)
        user_units_champions_owned = []

        # append multiple dataframes to lists 
        for id_min, id_max in zip(champion_id_min, champion_id_max):
            user_units_champions_owned.append(user_units_raw[user_units_raw["unit_id"].between(id_min, id_max)])
            
        # concat dataframes into one
        user_units_champions_owned = pd.concat(user_units_champions_owned)
            
        # keep only the specified columns and drop all others
        user_units_champions_owned = user_units_champions_owned.drop(user_units_champions_owned.columns.difference(["unit_id", "level"]), axis=1)
        # user_units_champions_owned.head()


        # 8
        # goal: unit_id | level | shards_sum(owned + available shards) | necessary shards | is_sufficient? | owned shards | flask_shard_sum | flask_count | source_1 | ... | source_n
        # ex:   Noctrus | 9     | 965                                  | 900              | True           | 750          | 225             | 2           | 2590... 2631
        # fancy print statements?

        # convert from shard_it to unit_id
        user_items_champion_shards["unit_id"] = user_items_champion_shards["id"] - 100000 # type: ignore

        # merge champ level and shards in inventory
        full_frame = user_units_champions_owned.merge(user_items_champion_shards, how="right") # type: ignore

        # merge shard sources and total form flasks
        full_frame = full_frame.merge(item_data_shards, how="left", left_on="id", right_on="reward")

        # replace NaN where possible
        for col in full_frame:
            if not re.match("source",col): # type: ignore
                full_frame[col] = full_frame[col].fillna(0).astype(int)

        # total shards available
        full_frame["shards_sum"] = full_frame["number"] + full_frame["flask_shards"]

        # shards for next upgrade
        def get_cost(level):
            return champion_costs[level]
        full_frame["upgrade_cost"] = full_frame["level"].apply(get_cost)

        # do you own enough, need flasks or just can't upgrade champ yet
        def is_sufficient(owned, potential, cost):
            if owned >= cost:
                return "yes! no flasks necessary!"
            elif owned + potential >= cost:
                return "yes, only with flasks"
            else:
                return f"no, missing {int(cost-potential-owned)}"
        full_frame["is_sufficient"] = full_frame.apply(lambda x: is_sufficient(x["number"], x["flask_shards"], x["upgrade_cost"]), axis=1)

        full_frame.tail()

        user_items_flasks.set_index("id", inplace=True) # only runnable once before issues arise
        # user_items_flasks.head()

        # create new dataframe, replacing all ids with readable item names
        # TODO: add amount of each flask in table after source

        # replace source ids and ignore NaN
        final = full_frame.copy()

        source_cols = [f"source_{i+1}" for i in range(sum([1 for col in final if re.match("source", col)]))] # type: ignore

        # array consisting of the source columns from the dataframe
        arr = []
        for col in source_cols:
            arr.append(final[col].values)

        # {2603.0: ('Tarragon Flask', 200), 2660.0: ('Enchanted Yaritza Flask', 20),...}
        replace_dict = {}
        for col in arr:
            for row in col:
                if not pd.isna(row):
                    if row not in replace_dict:
                        replace_dict[row] = (user_items_flasks.loc[row, "flask_name"], user_items_flasks.loc[row, "number"]) # shardbot exception perma fix here

        def replace_flask_id(flask_id):
            if not pd.isna(flask_id):
                return replace_dict[flask_id][0], replace_dict[flask_id][1]

        for i, col in enumerate(source_cols):
            final[[col,f"amount_{i}"]] = final[col].apply(replace_flask_id).apply(pd.Series) # type: ignore

        # end game
        # re-order columns
        # [('source_1', 'amount_1'), ('source_2', 'amount_2'), ('source_3', 'amount_3')]
        # BUG: wrong shard amounts in dictionary
        # source_amount_comprehension = [(f"source_{i}", f"amount_{i}") for i in range(1,max([len(val) for val in source_dict.values()])+1)]
        # source_amount_comprehension = list(itertools.chain(*source_amount_comprehension))

        source_amount_comprehension = [f"source_{i}" for i in range(1,max([len(val) for val in source_dict.values()])+1)]

        final = final.reindex(columns=["unit_id","level","shards_sum","upgrade_cost","is_sufficient","number","flask_shards"]+source_amount_comprehension)
        final = final.rename(columns={"unit_id":"champ", "number":"shards_owned"})

        # for col in source_amount_comprehension[1::2]:
        #     final[col] = final[col].fillna(0).astype(int)

        # use .xml to replace unit_id by champ name
        final.replace(cards_shard_dict, inplace=True)
        if not gui:
            print(final)
            input('Press Enter to exit...')
        else:
            return final # only if everything before worked correctly, without exceptions
    except Exception as e:
        print("Exception while processing data occured!\nCannot continue!\nCheck README.txt, then message IchChefDuNixx")
        print(e)
        print(e.args)
        print(e.with_traceback(e.__traceback__))
        input("Press ENTER to confirm this error and exit...")
        return
    
# %%
def not_main(gui:bool=False):

    # try to read credentials.json or create new file with default values
    default_credentials = {
        "user_id": "default",
        "password": "default"
        # "kong_token": "default_token" # placeholder for now
    }
    
    try:
        with open("credentials.json", "r") as f:
            credentials = json.load(f)
    except FileNotFoundError:
        with open("credentials.json", "w") as f:
            json.dump(default_credentials, f)
        credentials = default_credentials
        
    # ask for new values if they're still default
    if "default" in (credentials["user_id"], credentials["password"]):
        credentials["user_id"] = input("Please enter your USER_ID and press Enter:\n")
        credentials["password"] = input("\nPlese enter your PASSWORD* and press Enter:\n")
        with open("credentials.json", "w") as f:
            json.dump(credentials, f)
        print(f"\nUpdated credentials:\n{credentials}")
    else:
        print(f"Current credentials:\n{credentials}")
        
    # init request process
    
    # old version using input()
    # key = input("\nPress ENTER to update data... (or other buttons + Enter to use old data)")
    # if key != "":
    #     print("")#TODO: it's already calculating the result without updating!
    #     return
    # else:
    #     print("Request in progress...")
    
    # improved version using the readchar module
    print("\nPress ENTER to update data... (or any other button to immediately process the last snapshot)")
    if readchar.readkey() not in (readchar.key.ENTER, readchar.key.ENTER_2):
        # continue with old data
        return main(gui)
    # return here if main() had an error
    print("Request in progress...")

    try:
        response = requests.get(f"https://spellstone.synapse-games.com/api.php?message=init&user_id={credentials['user_id']}&password={credentials['password']}")
        
        response_content = json.loads(response.content.decode("utf-8"))
        
        points_of_interest = ["item_data", "user_items", "user_units"]
        for poi in points_of_interest:
            data = response_content[poi]
            with open(f"{poi}.json","w") as f:
                json.dump(data, f)
        
        # champ xml update    
        response = requests.get("https://spellstone.synapse-games.com/assets/cards_shard.xml")
        
        with open("cards_shard.xml", "wb") as file:
            file.write(response.content)
    
        input("Done! Press Enter to get your results!")
    except:
        print("\nError in the data request process!\nCannot continue!\nCheck/delete credentials.json and README.txt, then message IchChefDuNixx\n")
        input("Press ENTER to confirm the error and exit...")
    else:
        return main(gui)

# %%
if __name__ == "__main__":
    not_main()
    
# %% DO NOT REMOVE THIS NOTEBOOK MARKER!!!