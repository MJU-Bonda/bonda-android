### Snackbar 호출 방법
#### Activity 에서
```kotlin
binding.button.setOnClickListener{
    showSnackbar(SnackbarType.SAVE)
}
binding.button.setOnClickListener{
    showSnackbar(SnackbarType.BADGE)
}
binding.button.setOnClickListener{
    showSnackbar(SnackbarType.FAILED)
}
```

#### Fragment 에서
```kotlin
binding.button.setOnClickListener {
    (requireActivity() as AppCompatActivity)
        .showSnackbar(SnackbarType.SAVE)
}
binding.button.setOnClickListener {
    (requireActivity() as AppCompatActivity)
        .showSnackbar(SnackbarType.BADGE)
}
binding.button.setOnClickListener {
    (requireActivity() as AppCompatActivity)
        .showSnackbar(SnackbarType.FAILED)
}
```