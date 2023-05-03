# %%
# imports
import glob
import json
import traceback
from lxml import etree

import pandas as pd
from tabulate import tabulate

# run only when needed
def update_local_files():
    # create the main dictionary that will contain all items from other dictionaries
    all_dict = {}
    xml_files = glob.glob("cards_data/*.xml")

    for xml_file in xml_files:
        
        tree = etree.parse(xml_file) # type: ignore
        root = tree.getroot()
        
        for unit in root.findall("unit"):
            all_dict[int(unit.find("id").text)] = (unit.find("name").text, xml_file)
    # save
    with open("cards_data/cards_all.json", "w", encoding="utf-8") as f:
        json.dump(all_dict, f)

# the following are for use inside main()
def make_full_df(card_map, cards_all, item_data):
    df = pd.DataFrame.from_dict(card_map, orient="index")
    
    try:
        df["unit"] = df["unit_id"].apply(lambda x: cards_all[str(x)][0])
    except KeyError as e:
        missing_id = e.args[0]
        cards_all[missing_id] = [f"KeyError: {missing_id}", "cards_data/cards_KeyError.xml"] 
        
    # name: id -> string
    df["unit"] = df["unit_id"].apply(lambda x: cards_all[str(x)][0])

    # runes: dict -> description
    df["rune"] = df["runes"].apply(lambda x: item_data[x["1"]["item_id"]]["desc"].split(",")[0] if type(x) == dict else x)

    # level: remove for non-champs
    def _source_file_to_set(src:str):
        return src[17:src.index(".")]
    df["set"] = df["unit_id"].apply(lambda x: _source_file_to_set(cards_all[str(x)][1]))

    df.loc[df["set"] != "shard", "level"] = ""
    
    return df.reindex(["unit", "level", "rune", "cost"], axis=1)

def show_opponent_deck(df, current_host):        
    # TODO NOTE COMMENT FIXME BUG show dataframe containing remaining possible cards. eg deck - played ones or parse all turn in arena
    
    # make dataframe of enemy deck
    if current_host == "2753859001":
        df = df.loc["101":]
    else:
        df = df.loc[:"101"]
    
    # print fancy table in console
    df = df.reindex(["unit", "level", "rune", "cost"], axis=1)
    table = tabulate(df, headers="keys", tablefmt="orgtbl") # type: ignore
    
    # calc stats about deck
    num_champs = df["level"][df["level"] != ""].count()
    most_used_rune = df["rune"].value_counts()
    most_used_rune_count = str(most_used_rune.index[0]) + " (" + str(most_used_rune.iloc[0]) + ")"
    cost_distribution = df["cost"].value_counts().to_dict()
    cost_distribution = [cost_distribution.get(i, 0) for i in range(5)]
    cost_distribution_str = " | ".join(str(v) for v in cost_distribution)
    deck_overview = f"Champs: {num_champs}\nmost used rune: {most_used_rune_count}\ncost distribution: {cost_distribution_str}"

    return "opponent deck\n" + table + "\n" + deck_overview

def get_starting_cards(on_hand, cards_all, card_map):        
    on_hand_str = "|| "
    for init_card in on_hand:
        on_hand_str += cards_all[card_map[str(init_card)]["unit_id"]][0] + " - "
    return on_hand_str[:-3] + " ||"
    
def get_new_card(df:pd.DataFrame, on_hand):
    if len(on_hand) != 1:
        return "DONTLOG"
    new_card = df.loc[str(on_hand[0])]
    unit, level, rune, cost = new_card.values
    return f"{unit}"# {level, rune, cost}"
    
def main(responseData:dict):
    try:
        # load
        with open("cards_data/cards_all.json", "r", encoding="utf-8") as f:
            cards_all = json.load(f)
            
        with open("cards_data/item_data.json", "r", encoding="utf-8") as f:
            item_data = json.load(f)
        
        message = responseData["data"]
        # ping pong test
        if message == "ping":
            return "pong"
        
        url = responseData["url"]
        
        # remove domain, path and query from url
        file_name = url[url.rindex("/")+1:url.index("?")]
        
        # prevent crash/ not loading issues
        if message[0] == "<":
            # saving file
            with open(f"cards_data/{file_name}", "w", encoding="utf-8") as f:
                f.write(message)
            
            # check for one of the last files before running upate
            # once per loading the game, might need to do twice
            if file_name == "cards_shard.xml":
                update_local_files()
                
            # should be logged 15x
            return "updating local files..."
        
        elif url.find("message=init") != -1:
            # it's not always sent
            message_old = json.loads(message)
            if "item_data" in message_old.keys():
                with open("cards_data/item_data.json", "w", encoding="utf-8") as f:
                    json.dump(message_old["item_data"], f)
                # should be logged 1x
                return "updating local files..."

        msg_dict = json.loads(message)
        request_type = msg_dict["request"]["message"]
        # in arena, host is always the attacker
        if "battle_data" in msg_dict.keys() and "host_id" in msg_dict["battle_data"].keys():
            current_host = msg_dict["battle_data"]["host_id"] # 2753859001 == IchChefDuNixx
        else:
            current_host = "0"
        
        if "battle_data" in msg_dict.keys():
            card_map = msg_dict["battle_data"]["card_map"]
            turn = msg_dict["battle_data"]["turn"]
        else:
            return "DONTLOG"

        # BUG: log stops posting after opponent goes autoplay? probably fixed
        # NOTE: only working for placement? and arena
        # hand cards

        on_hand = []
        on_hand_auto = []
    
        # get index of most recent turn
        i = len(turn.keys())
        is_tower_battle = False
        
        # atm -1 unique to tower battles (tower card 151)
        if "-1" in turn.keys():
            i = i-2
            is_tower_battle = True
        
        full_df = make_full_df(card_map, cards_all, item_data)

        # make alt list in case opponent goes autoplay
        if "draws" in turn[str(i)].keys():
            on_hand.extend(turn[str(i)]["draws"])
            # edge case for autoplay and handling the last round in bounties
            if i > 1 and len(turn[str(i-1)]) > 0 and "draws" in turn[str(i-1)].keys():
                on_hand_auto.extend(turn[str(i-1)]["draws"])
            # else:
                # "WON"
                
        # only once, prevent going to next if statement
        if i == 1:
            return show_opponent_deck(full_df, current_host)
        
        # NOTE: only in arena so far?
        # initial 3 cards
        if len(on_hand) == 3:
            if (current_host == "2753859001"): # me starting, ignore 1-15
                if on_hand[0] >= 100:
                    return get_starting_cards(on_hand, cards_all, card_map)
                else:
                    return get_starting_cards(on_hand_auto, cards_all, card_map)
                    
            else: # opponent is host, opponent is starting, ignore 101-115
                if on_hand[0] <= 100:
                    return get_starting_cards(on_hand, cards_all, card_map)
                else:
                    return get_starting_cards(on_hand_auto, cards_all, card_map)
                
        # disregard console logs about own cards
        elif len(on_hand) > 0:
            if is_tower_battle:
                return "DONTLOG"
            
            elif current_host == "2753859001": # me starting, ignore 1-15
                if on_hand[0] >= 100:
                    return get_new_card(full_df, on_hand)
                else:
                    return get_new_card(full_df, on_hand_auto)
                    
            else: # opponent is host, opponent is starting, ignore 101-115
                if on_hand[0] <= 100:
                    return get_new_card(full_df, on_hand)
                else:
                    return get_new_card(full_df, on_hand_auto)
                
        else:
            # condition for when victory is guaranteed
            # possible to move up to display each fight
            if i >= 1 and len(turn[str(i-1)]) == 0:
                return "WON!"
            
            # (maybe) condition for guaranteed defeat. but also for arena every round
            if i >= 2 and len(turn[str(i-2)]) == 0:
                return "DONTLOG"
          
            # at the end of a long fight
            if len(on_hand) == 0:
                return "All cards drawn"

    except Exception as e:
        return traceback.format_exc()        
# %%
