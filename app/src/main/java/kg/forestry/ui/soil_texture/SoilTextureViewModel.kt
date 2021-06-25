package kg.forestry.ui.soil_texture

import kg.core.Event
import kg.forestry.ui.core.base.BaseViewModel
import kg.forestry.localstorage.model.SoilTexture

class SoilTextureViewModel: BaseViewModel<Event>() {

    var soilTexture: SoilTexture?=null
}
