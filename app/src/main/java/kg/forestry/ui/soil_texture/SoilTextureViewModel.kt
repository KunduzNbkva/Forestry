package kg.forestry.ui.soil_texture

import kg.core.Event
import kg.core.base.BaseViewModel
import kg.forestry.localstorage.model.SoilTexture

class SoilTextureViewModel: BaseViewModel<Event>() {

    var soilTexture: SoilTexture?=null
}
