#include <stdio.h>
#include <stdlib.h>
#include "linkedlist.h"
#include "hashtable.h"

struct hashtable
{
    // Hashtable uses linkedlist as buckets
    linkedlist_t **buckets;
    unsigned int num_buckets;
    unsigned int size;
};

/**
 * Hash function to hash a key into the range [0, max_range)
 */
static int hash(int key, int max_range)
{
    key = (key > 0) ? key : -key;
    return key % max_range;
}

hashtable_t *ht_init(int num_buckets)
{
    // Creates a new hashtable
    linkedlist_t **buckets = malloc(num_buckets * sizeof(linkedlist_t *));

    for (int i = 0; i < num_buckets; i++)
    {
        buckets[i] = ll_init();
    }

    hashtable_t *table = malloc(sizeof(hashtable_t));
    table->buckets = buckets;
    table->num_buckets = num_buckets;
    table->size = 0;
    return table;
}

void ht_add(hashtable_t *table, int key, int value)
{
    // Creates a new mapping from key -> value.
    // If the key already exists, then it replaces the value.
    int index = hash(key, table->num_buckets);
    linkedlist_t *list = table->buckets[index];

    int prev_size = ll_size(list);
    ll_add(list, key, value);
    if (ll_size(list) > prev_size)
    {
        table->size++;
    }
}

int ht_get(hashtable_t *table, int key)
{
    // Retrieves the value mapped to the given key.
    // If it does not exist, returns 0
    int index = hash(key, table->num_buckets);
    linkedlist_t *list = table->buckets[index];
    return ll_get(list, key);
}

int ht_size(hashtable_t *table)
{
    // Returns the number of mappings in this hashtable
    return table->size;
}
