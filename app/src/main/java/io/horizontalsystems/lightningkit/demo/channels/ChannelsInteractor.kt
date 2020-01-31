package io.horizontalsystems.lightningkit.demo.channels

import androidx.lifecycle.ViewModel
import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class ChannelsInteractor(private val lightningKit: LightningKit) : ViewModel(), ChannelsModule.IInteractor {
    lateinit var delegate: ChannelsModule.IInteractorDelegate

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

    override fun subscribeToStatusUpdates() {
        lightningKit.statusObservable
            .subscribe {
                delegate.onStatusUpdate(it)
            }
            .let {
                disposables.add(it)
            }
    }

    override fun subscribeToChannelUpdates() {
        lightningKit.channelsObservable
            .subscribe {
                delegate.onChannelsUpdate(it)
            }
            .let {
                disposables.add(it)
            }
    }

    override fun clear() {
        disposables.clear()
    }
}
