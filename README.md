# MoneyEditText

Dealing with the decimal money always is painful in Android. The EditText is bugged and only decimal separator it is aware of is the US decimal separator. To overcome this issue we use the ReplacementSpan and do not modify original text in the widget. Thanks to @nexus700120 for the idea and basic implementation.

<img src="/images/sample.gif" alt="Sample" width="300px" />

Usage
-----

1. Add jcenter() to repositories block in your gradle file.
2. Add `compile 'com.shuhart.moneyedittext:moneyedittext-kotlin:1.0'` to your dependencies.
2. Add `MoneyEditText` into your layouts or view hierarchy.
3. Look into the sample for additional details on how to use and configure the library.

License
=======

    Copyright 2017 Bogdan Kornev.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
