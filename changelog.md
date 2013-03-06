Changelog
=========

v0.5b - 27.02.13
----------------

- Added support for going from controller `actions()` to `CAction` via `application.path.of.classname`.
- Several fixes.

v0.4.2b - 23.02.13
------------------

- Added support for multilevel controller paths like `controllers/path/to/controller`.

v0.4.1b - 22.02.13
------------------

- Added support for themes when navigating from `Controller::render[Partial]()`.
  It now leads to `protected/themes/themename/views/view`.

v0.4b - 20.02.13
----------------

- `$this->widget("Class")` fix.
- Added support for `widget::render()` and `widget::renderPartial()`.
- Added support for `render('/absolute/module/path/to/view')`.
- Added support for Smarty templates in `render('view.tpl')`.

v0.4a
-----

- Added support for going from `CAction` `render` and `renderPartial` to view for
  the first Controller using that `CAction`. First time search can be slow, ~200-1000ms.
  After it's done everything is cached till PhpStorm is closed.
- Added support for going from `$this->widget('path.to.widget.Class')` to widget class file.
- Added support for going from a view to another view via `$this->render` and `$this->renderPartial`.

v0.3
----

- Added support for going from controller to view via view name in `render` and `renderPartial`.
- Added support for going from relation to related model through the class name.