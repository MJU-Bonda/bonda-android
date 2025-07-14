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

showSnackbar(
    message = "파일이 업로드되었습니다.",
    buttonText = "확인",
    onButtonClick = {
        // 버튼 클릭 시 동작
        Toast.makeText(this, "확인 클릭됨", Toast.LENGTH_SHORT).show()
    },
    type = SnackbarType.SAVE
)
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