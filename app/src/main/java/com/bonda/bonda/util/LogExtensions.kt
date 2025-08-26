package com.bonda.bonda.util

import android.util.Log

/**
 * 에러를 지정된 형식으로 출력합니다
 * 로그 형식: "파일명::메소드명" + "오류 메시지"
 */
fun Throwable.logError() {
    // 현재 스레드의 스택 트레이스 정보를 가져옵니다.
    val stackTrace = Thread.currentThread().stackTrace

    // 스택 트레이스에서 실제 호출 지점을 찾습니다.
    // stackTrace[0]은 getStackTrace, [1]은 이 logError 함수 자신이므로 그 이후를 찾아야 합니다.
    // 보통 [3] 또는 [4]에 실제 호출한 곳의 정보가 있습니다.
    // 더 견고하게 만들려면, 이 확장 함수가 선언된 파일명을 제외하고 처음 나오는 클래스를 찾습니다.
    val caller = stackTrace.find {
        !it.className.contains(this::class.java.name) && // 확장 함수가 포함된 클래스 제외
                !it.fileName.isNullOrEmpty()
    }

    // 호출자 정보가 있으면 파일명과 메소드명을 조합하고, 없으면 기본 메시지를 사용합니다.
    val logMessage = caller?.let {
        "${it.fileName?.substringBefore(".kt")}::${it.methodName}"
    } ?: "Unknown location"

    // 최종적으로 Log.e를 호출합니다.
    // this는 Throwable 자신을 의미하므로, 스택 트레이스 전체가 함께 출력됩니다.
    Log.e(TAG, logMessage, this)
}