package io.horizontalsystems.lightningkit.demo.openchannel

import io.horizontalsystems.lightningkit.LightningKit
import io.reactivex.disposables.CompositeDisposable

class OpenChannelInteractor(private val lightningKit: LightningKit) : OpenChannel.IInteractor {
    lateinit var delegate: OpenChannelPresenter

    private val disposables = CompositeDisposable()

    override fun openChannel(capacity: Long, nodePublicKey: String, nodeAddress: String) {
        lightningKit
            .openChannel(nodePublicKey, capacity, nodeAddress)
            .subscribe({
                delegate.onOpenChannelSuccess(it)
            }, {
                delegate.onOpenChannelFailed(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun clear() {
        disposables.dispose()
    }
}
