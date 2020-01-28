package com.sample.newsapp.domain

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean

//all domain logic backed by Rxjava observables. Invoke them from kotlin
typealias UseCase<T, U> = (T) -> Observable<U>

//mediation usecase for Android UI components to interact
interface MediationUseCase<T, U> {
    private companion object {
        val _status = MutableLiveData<Boolean>()
    }

    fun execute(input: T): Boolean
    fun data(): LiveData<Result<U>>
    fun status(): LiveData<Boolean> = _status
    fun dispose()
}

// domain logic by passing data via bundle
interface BundleUseCase<T> : UseCase<Bundle, T>


//Conversion of usecase to Mediation backed by Rxjava
internal fun <T, U> UseCase<T, U>.toMediator(
    ignoreNextReq: Boolean = false,
    scheduler: Scheduler = Schedulers.io()
): MediationUseCase<T, U> {
    return object : MediationUseCase<T, U> {
        private val disposable = CompositeDisposable()
        private val _data = MutableLiveData<Result<U>>()
        private val _status = MutableLiveData<Boolean>()
        private val reqInProg = AtomicBoolean(false)


        override fun execute(input: T): Boolean {
            if (ignoreNextReq && !reqInProg.compareAndSet(false, true)) {
                return false
            }

            return disposable.add(
                invoke(input).subscribeOn(scheduler)
                    .subscribe({
                        _data.postValue(Result.success(it))
                    }, {
                        _data.postValue(Result.failure(it))
                        inProg(false)
                    }, {
                        inProg(false)
                    }, {
                        inProg(true)
                    })
            )
        }

        private fun inProg(state: Boolean) {
            _status.postValue(state)
            reqInProg.set(state)
        }

        override fun data(): LiveData<Result<U>> = _data

        override fun status(): LiveData<Boolean> = _status

        override fun dispose() = disposable.dispose()
    }
}

//mediation for DB and the UI. With LiveDataReactiveStreams
internal fun <T, U> UseCase<T, U>.toMediatorDB(
    scheduler: Scheduler = Schedulers.io()
): MediationUseCase<T, U> {
    return object : MediationUseCase<T, U> {
        private var CRUD: LiveData<U> = MutableLiveData()
        private val mediator = MediatorLiveData<Result<U>>()
        private val status = MutableLiveData<Boolean>()

        override fun execute(input: T): Boolean {
            mediator.removeSource(CRUD)
            val obs = invoke(input).subscribeOn(scheduler)
                .onErrorResumeNext(Observable.empty())
            CRUD =
                LiveDataReactiveStreams.fromPublisher(obs.toFlowable(BackpressureStrategy.BUFFER))
            mediator.addSource(CRUD) {
                mediator.postValue(Result.success(it))
            }
            return true
        }

        override fun status(): LiveData<Boolean> = status

        override fun data(): LiveData<Result<U>> = mediator

        override fun dispose() {}
    }
}

fun disposeUseCases(vararg uc: MediationUseCase<*, *>) = uc.forEach { it.dispose() }
