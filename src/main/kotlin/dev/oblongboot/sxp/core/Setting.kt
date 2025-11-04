package dev.oblongboot.sxp.core

abstract class Setting<T>(
    override val name: String,
    override val description: String,
    val default: T,
    override var x: Int = 0,
    override var y: Int = 0
) : Element {
    protected var _value: T = default
    
    open var value: T
        get() = _value
        set(newValue) {
            val oldValue = _value
            _value = newValue
            onValueChanged(oldValue, newValue)
        }
    
    protected open fun onValueChanged(oldValue: T, newValue: T) {

    }
    
    abstract fun reset()
}