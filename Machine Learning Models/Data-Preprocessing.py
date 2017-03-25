# coding: utf-8

# In[1]:

import numpy as np
import pandas as pd
from collections import defaultdict


# In[2]:

policy_df = pd.read_csv("policy.csv")
data_df = pd.read_csv("data.csv")


# In[3]:

def cardprice(df):
    pricepoints = dict()
    cardnames = df.columns.values.tolist()
    for i in range(1,len(df.columns.values)):
        pricepoints[int(df[cardnames[i]][4])] = cardnames[i]
    return pricepoints


# In[4]:

cardprices = cardprice(policy_df)


# In[5]:

def recommend_card(cdf):
    cards = defaultdict(list)
    for i in range(len(cdf)):
        if ((cdf["Annual salary"][i] / 12 >= 15000) & (cdf["Age"][i] >= 18) & (cdf["Age"][i] <= 55) & (cdf["Occupation"][i] == "Self-employed") | (cdf["Occupation"][i] == "Salaried") & (cdf["Company category"][i] == "C") | (cdf["Company category"][i] == "D")):
            cards[i].append("Card 4")
        if ((cdf["Age"][i] >= 19) & (cdf["Age"][i] <= 60) & (cdf["Occupation"][i] == "Self-employed") & (cdf["Annual salary"][i] / 12 >= 100000)  & (cdf["Company category"][i] == "A")):
            cards[i].append("Card 5")
        if ((cdf["Age"][i] >= 21) & (cdf["Age"][i] <= 50) & (cdf["Occupation"][i] == "employed") & (cdf["Annual salary"][i] / 12 >= 80000)  & (cdf["Company category"][i] == "A") | (cdf["Company category"][i] == "B")):
            cards[i].append("Card 3")
        if ((cdf["Age"][i] >= 20) & (cdf["Age"][i] <= 60) & (cdf["Occupation"][i] == "Salaried") | (cdf["Occupation"][i] == "Self-employed") & (cdf["Annual salary"][i] / 12 >= 60000) & (cdf["Company category"][i] == "A") | (cdf["Company category"][i] == "B") | (cdf["Company category"][i] == "C")):
            cards[i].append("Card 2")
        if ((cdf["Age"][i] >= 18) & (cdf["Age"][i] <= 55) & (cdf["Occupation"][i] == "Self-employed") & (cdf["Annual salary"][i] / 12 >= 45000) & (cdf["Company category"][i] == "A")):
            cards[i].append("Card 1")
    
    result1 = dict()
    result2 = dict()
    fin = dict()
    for customerid, card in cards.iteritems():
	for icard in card:
		try:
			if(len(icard) > 1) and fin.get(customerid) != None:
				fin[customerid] = str(fin.get(customerid)) + "," + icard
			else:
				fin[customerid] = icard
		except:
			raise
    
    for customerid, card in fin.iteritems():
        result1[customerid + 1] = card
    submission1 = pd.DataFrame({
            "Customer name": list(result1.keys()),
            "Recommended Card": list(result1.values())
        })
    submission1.to_csv('submission1.csv', index=False)
    
    finalizedcard = finalize_card(cards)
    for customerid, card in finalizedcard.iteritems():
        result2[customerid + 1] = card
    submission2 = pd.DataFrame({
            "Customer name": list(result2.keys()),
            "Recommended Card": list(result2.values())
        })
    submission2.to_csv('submission2.csv', index=False)


# In[6]:

def finalize_card(cards):
    finalcard = defaultdict(list)
    fcard = dict()
    for key,val in cards.items():
        if len(val) > 1:
            for card in val:
                finalcard[key].append(policy_df[card][4])
                
    fcard = {customer_id: int(max(cards, key=int)) for customer_id, cards in finalcard.iteritems()}
    
    for customerid, price in fcard.iteritems():
        fcard[customerid] = cardprices[price]
    return fcard
    
    
            


# In[ ]:

recommend_card(data_df)

