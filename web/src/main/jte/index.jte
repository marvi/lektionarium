@param lectio.web.DayView day
@param java.util.List<Integer> years
@param String feedWebcal
@template.layout(
  title = day.name() + " – Lektionarium",
  years = years,
  feedWebcal = feedWebcal,
  content = @`
    @template.dagkort(day = day)
  `)
