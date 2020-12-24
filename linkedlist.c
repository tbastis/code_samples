// Written by Thomas Bastis
// This code implements a Linked List which can hold integers (for use in
// hashtable.c)

#include <stdio.h>
#include <stdlib.h>
#include "linkedlist.h"

struct linkedlist
{
    struct linkedlist_node *first;
    unsigned int size;
};

struct linkedlist_node
{
    unsigned int key;
    int value;
    struct linkedlist_node *next;
};
typedef struct linkedlist_node linkedlist_node_t;

linkedlist_t *ll_init()
{
    // Creates a new linked list
    linkedlist_t *list = malloc(sizeof(linkedlist_t));
    list->first = NULL;
    list->size = 0;
    return list;
}

void ll_add(linkedlist_t *list, int key, int value)
{
    // Creates a new node and adds it to the front of the linked list if a
    // node with the same key does not already exist.
    // Otherwise, replaces the existing value with the new value.
    linkedlist_node_t *step = list->first;
    while (step != NULL)
    {
        if (step->key == key)
        {
            step->value = value;
            return;
        }
        step = step->next;
    }

    linkedlist_node_t *node = malloc(sizeof(linkedlist_node_t));

    node->key = key;
    node->value = value;
    node->next = list->first;

    list->first = node;
    list->size++;
}

int ll_get(linkedlist_t *list, int key)
{
    // Goes through each node in the linked list and return the value of
    // the node with a matching key.
    // If it does not exist, returns 0.
    linkedlist_node_t *step = list->first;
    while (step != NULL)
    {
        if (step->key == key)
        {
            return step->value;
        }
        step = step->next;
    }
    return 0;
}

int ll_size(linkedlist_t *list)
{
    return list->size;
}
