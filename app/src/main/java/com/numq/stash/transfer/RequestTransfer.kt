package com.numq.stash.transfer

import com.numq.stash.action.CancellableAction
import com.numq.stash.extension.action
import com.numq.stash.interactor.UseCase

class RequestTransfer constructor(
    private val service: TransferService
) : UseCase<TransferAction, CancellableAction>() {
    override suspend fun execute(arg: TransferAction) = service.requestTransfer(arg).action()
}