package com.kavin.noteit.noteit

import android.support.v4.view.ViewCompat
import android.view.View
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject


class Animate {
        fun fadeIn(view: View, duration: Long): Completable {
            val animationSubject = CompletableSubject.create()
            return animationSubject.doOnSubscribe {
                ViewCompat.animate(view)
                        .setDuration(duration)
                        .alpha(1f)
                        .withEndAction {
                            animationSubject.onComplete()
                        }
            }
        }
}