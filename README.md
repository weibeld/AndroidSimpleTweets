# Project 3 - *Simple Tweets*

**Simple Tweets** is an Android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **25** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x]	User can **sign in to Twitter** using OAuth login
* [x]	User can **view tweets from their home timeline**
  * [x] User is displayed the username, name, and body for each tweet
  * [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
  * [x] User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView). Number of tweets is unlimited.
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* [x] User can **compose and post a new tweet**
  * [x] User can click a “Compose” icon in the Action Bar on the top right
  * [x] User can then enter a new tweet and post this to twitter
  * [x] User is taken back to home timeline with **new tweet visible** in timeline

The following **optional** features are implemented:

* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [ ] User can tap a tweet to **open a detailed tweet view**
* [ ] User can **select "reply" from detail view to respond to a tweet**
* [x] Improve the user interface and theme the app to feel "twitter branded"

The following **bonus** features are implemented:

* [ ] User can see embedded image media within the tweet detail view
* [ ] User can watch embedded video within the tweet
* [ ] Compose tweet functionality is build using modal overlay
* [ ] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [x] [Leverage RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) as a replacement for the ListView and ArrayAdapter for all lists of tweets.
* [x] Move the "Compose" action to a [FloatingActionButton](https://github.com/codepath/android_guides/wiki/Floating-Action-Buttons) instead of on the AppBar.
* [ ] On the Twitter timeline, leverage the [CoordinatorLayout](http://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events) to apply scrolling behavior that [hides / shows the toolbar](http://guides.codepath.com/android/Using-the-App-ToolBar#reacting-to-scroll).
* [ ] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.com/android/Drawables#vector-drawables) where appropriate.
* [x] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [ ] Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.
* [ ] Enable your app to [receive implicit intents](http://guides.codepath.com/android/Using-Intents-to-Create-Flows#receiving-implicit-intents) from other apps.  When a link is shared from a web browser, it should pre-fill the text and title of the web page when composing a tweet.
* [ ] When a user leaves the compose view without publishing and there is existing text, prompt to save or delete the draft.  The draft can be resumed from the compose view.

The following **additional** features are implemented:

* [x] In *offline mode*, the date and time of the last update (fetching of tweets starting from page 1) is indicated
* [x] Use [Retrolambda](https://github.com/evant/gradle-retrolambda) to replace all anonymous classes implementing a functional interface with a lambda expression


## Video Walkthrough

Here's a walkthrough of the implemented user stories:

![GIF walkthrough](assets/walkthrough.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

- The [Codepath OAuth Handler](https://github.com/codepath/android-oauth-handler) library uses Android Async HTTP as its HTTP client, which cannot be hooked up with [Stetho](http://facebook.github.io/stetho/). This is quite a disadvantage, because Stetho is really great for monitoring HTTP API calls. So, the next time, I would implement the OAuth functionality directly with ScribeJava and use Retrofit as the HTTP client.

## Open-source libraries used

- [Codepath OAuth Handler](https://github.com/codepath/android-oauth-handler) - Android library for simplifying the handling of OAuth requests (uses ScribeJava and Android Async HTTP)
- [ScribeJava](https://github.com/scribejava/scribejava) - Simple OAuth client Java library
- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Gson](https://github.com/google/gson) - Java serialization/deserialization library for converting JSON objects to Java objects and vice versa
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android
- [DBFlow](https://github.com/Raizlabs/DBFlow) - Object-relational mapper (ORM) for Android
- [Retrolambda](https://github.com/evant/gradle-retrolambda) - Enable lambda expressions and method references for Java < 8
- [Stetho](http://facebook.github.io/stetho/) - Chrome-based Android debugging bridge (used for inspecting SQLite database)

## License

    Copyright 2017 Daniel Weibel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
