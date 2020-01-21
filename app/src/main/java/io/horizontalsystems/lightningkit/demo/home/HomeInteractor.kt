package io.horizontalsystems.lightningkit.demo.home

import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class HomeInteractor(private val lightningKit: LightningKit) : ViewModel(), HomeModule.IInteractor {
    lateinit var delegate: HomeModule.IInteractorDelegate

    private val disposables = CompositeDisposable()

    override fun listChannels() {
        lightningKit.listChannels()
            .subscribe({
                delegate.onReceiveChannels(it)
            }, {
                delegate.onReceivedError(it)
            })
            .let {
                disposables.add(it)
            }
    }

    // ViewModel

    override fun clear() {
        disposables.clear()
    }
}
