{
    types: {
        'FamilyMember': {
            pluralLabel: 'FamilyMembers'
        }
    },
    properties: {
        'imageURL': {
            valueType: "url"
        },
        'url': {
            valueType: "url"
        },
        'author': {
            label:                  "authored by",
            reverseLabel:           "author of",
            reversePluralLabel:     "authors of",
            groupingLabel:          "their authors",
            reverseGroupingLabel:   "their work",
            valueType:              "item"
        },
        'DOB': {
            valueType:              "date"
        }
    }
}
