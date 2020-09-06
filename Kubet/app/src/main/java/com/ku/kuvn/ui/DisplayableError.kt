package com.ku.kuvn.ui

data class DisplayableError(val visible: Boolean,
                            val action: (() -> Unit)? = null)