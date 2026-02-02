Not to be confused with [Google Accompanist](https://github.com/google/accompanist). I simply like the name while being generally unable to make up my own names.

# HeroBrine1st's Accompanist

This is an opinionated collection of my own libraries related to Compose UI that are not big enough to be in their own repository but are reused multiple times in my own projects. I'm tired of copying them and so here it is. For example, [autocomplete](autocomplete) is used in three projects (two of them never reached public) in its highly specialised form.

## Autocomplete

```kotlin
dependencies {
    implementation("ru.herobrine1st.accompanist:autocomplete:0.1.5")
}
```

Usage:

https://github.com/HeroBrine1st/accompanist/blob/bd0d267015dc8f52920cad75c3c03b6a9a5a66cf/sample/src/commonMain/kotlin/ru/herobrine1st/accompanist/App.kt#L45-L60