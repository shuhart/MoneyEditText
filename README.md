# MoneyEditText

Dealing with the decimal money always is painful in Android. The EditText is bugged and only decimal separator it is aware of is the US decimal separator. To overcome this issue we use the ReplacementSpan and do not modify original text in the widget. Thanks to @nexus700120 for the idea and basic implementation.

<img src="/images/sample.gif" alt="Sample" width="300px" />

Usage
-----

1. Add jcenter() to repositories block in your gradle file.
2. Add `compile 'com.shuhart.moneyedittext:moneyedittext-kotlin:1.0'` to your dependencies.
2. Add `MoneyEditText` into your layouts or view hierarchy.
3. Look into the sample for additional details on how to use and configure the library.
