package com.principate.midas.lib
package newtypes

import monocle.Iso

trait Wrapper[A, B]:
  def iso: Iso[A, B]
end Wrapper
