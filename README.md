# MoneyEditText

Dealing with the decimal money is such a pain in Android. The EditText is bugged and only decimal separator it is aware of is the US decimal separator. To overcome this issue we use the ReplacementSpan and do not modify original text in the widget. Thanks to @nexus700120 for the idea and basic implementation.
